package com.example.OnlineProctoring.serviceImpl;

import com.example.OnlineProctoring.customExceptions.ExamQuestionEmptyException;
import com.example.OnlineProctoring.models.*;
import com.example.OnlineProctoring.repository.AnswerOptionRepository;
import com.example.OnlineProctoring.repository.ExamRepository;
import com.example.OnlineProctoring.repository.QuestionsRepository;
import com.example.OnlineProctoring.repository.UserRepository;
import com.example.OnlineProctoring.service.AttachmentService;
import com.example.OnlineProctoring.service.ExamService;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private static final Logger logger = LoggerFactory.getLogger(ExamServiceImpl.class);

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionsRepository questionsRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttachmentService attachmentService;

    @Override
    public void createExam(ExamDTO examDTO, String userName) throws Exception {
        logger.info("Inside CreateExam method of ExamServiceImpl");
        try {
            if(userName != null) {
                UserAuth userAuth = userRepository.findByUserName(userName);
                if(userAuth != null) {
                    Exam exam = new Exam();
                    exam.setTitle(examDTO.getTitle());
                    exam.setDescription(examDTO.getDescription());
                    exam.setDurationSeconds(examDTO.getDurationInSeconds());
                    exam.setStatus(examDTO.getStatus());
                    exam.setShuffleQuestions(examDTO.isShuffleQuestions());
                    exam.setNegativeMarkingAllowed(examDTO.isNegativeMarkingAllowed());
                    exam.setSubject(examDTO.getSubject());
                    exam.setNumberOfQuestions(examDTO.getNumberOfQuestion());
                    exam.setActiveFlag(true);
                    exam.setCreatedAt(LocalDateTime.now());
                    exam.setUpdatedAt(null);
                    exam.setStartDate(examDTO.getStartDateTime());
                    exam.setEndDate(examDTO.getEndDateTime());
                    exam.setCreatedBy(userAuth.getUserId());

                    if(examDTO.getQuestionAnswerAttachment() != null) {
                        logger.info("Inside Excel Question Insert Condition for Exam Creation");

                        Attachment attachment = new Attachment();
                        String fileName = examDTO.getQuestionAnswerAttachment().getOriginalFilename();
                        assert fileName != null;
                        int index = fileName.lastIndexOf(".");
                        attachment.setOriginalFileName(fileName);
                        attachment.setContentType(examDTO.getQuestionAnswerAttachment().getContentType());
                        if(index != -1) {
                            attachment.setFileExtension(fileName.substring(index));
                        }
                        attachment.setCreatedDate(LocalDateTime.now());
                        byte[] fileByteArray = examDTO.getQuestionAnswerAttachment().getBytes();
                        attachment.setContentSize(String.valueOf
                                (examDTO.getQuestionAnswerAttachment().getSize()));
                        attachment = attachmentService.uploadAttachment(attachment, fileByteArray);

                        exam.setAttachmentId(attachment.getAttachmentId());
                        examRepository.saveAndFlush(exam);

                        List<QuestionsDTO> questionsDTOList = generateQuestionAnswerFromFileBytes
                                (examDTO.getQuestionAnswerAttachment().getBytes());

                        for(QuestionsDTO questionsDTO: questionsDTOList) {
                            Questions questions = new Questions();
                            List<AnswerOption> answerOptionList = new ArrayList<>();
                            questions.setQuestionType(questionsDTO.getQuestionType());
                            questions.setQuestionText(questionsDTO.getQuestionText());
                            questions.setDefaultMarks(questionsDTO.getDefaultMarks());
                            questions.setDefaultNegativeMarks(questionsDTO.getDefaultNegativeMarks());
                            questions.setDifficulty(questionsDTO.getDifficulty());
                            questions.setSubject(questionsDTO.getSubject());
                            questions.setExplanation(questionsDTO.getExplanation());
                            questions.setActiveFlag(true);
                            questions.setCreatedAt(LocalDateTime.now());
                            questions.setCreatedBy(userAuth.getUserId());
                            questions.setUpdatedAt(null);
                            questions.setExam(exam);

                            questionsRepository.saveAndFlush(questions);
                            if(questionsDTO.getAnswerOptionDTOList() != null
                                    && !questionsDTO.getAnswerOptionDTOList().isEmpty()) {
                                for(AnswerOptionDTO answerOptionDTO: questionsDTO.getAnswerOptionDTOList()) {
                                    AnswerOption answerOption = new AnswerOption();
                                    answerOption.setOptionDetails(answerOptionDTO.getOptionDetails());
                                    answerOption.setCorrect(answerOptionDTO.isCorrect());
                                    answerOption.setDisplayOrder(answerOptionDTO.getDisplayOrder());
                                    answerOption.setCreatedAt(LocalDateTime.now());
                                    answerOption.setCreatedBy(userAuth.getUserId());
                                    answerOption.setUpdatedAt(null);
                                    answerOption.setActiveFlag(true);
                                    answerOption.setQuestions(questions);

                                    answerOptionList.add(answerOption);
                                }
                            } else {
                                throw new ExamQuestionEmptyException("Please Provide Valid Question/Answers for Exam Creation.");
                            }

                            answerOptionRepository.saveAllAndFlush(answerOptionList);
                        }
                        logger.info("Outside Excel Upload Insert Condition for Exam Creation");
                    } else if((examDTO.getQuestionsList() != null && !examDTO.getQuestionsList().isEmpty())) {
                        examRepository.saveAndFlush(exam);

                        logger.info("Inside Manual Insert Condition for Exam Creation");
                        for(QuestionsDTO questionsDTO: examDTO.getQuestionsList()) {
                            Questions questions = new Questions();
                            List<AnswerOption> answerOptionList = new ArrayList<>();
                            questions.setQuestionType(QuestionType.MCQ_SINGLE);
                            questions.setQuestionText(questionsDTO.getQuestionText());
                            questions.setDefaultMarks(questionsDTO.getDefaultMarks());
                            questions.setDefaultNegativeMarks(questionsDTO.getDefaultNegativeMarks());
                            questions.setDifficulty(questionsDTO.getDifficulty());
                            questions.setSubject(questionsDTO.getSubject());
                            questions.setExplanation(questionsDTO.getExplanation());
                            questions.setActiveFlag(true);
                            questions.setCreatedAt(LocalDateTime.now());
                            questions.setUpdatedAt(null);
                            questions.setCreatedBy(userAuth.getUserId());
                            questions.setExam(exam);

                            questionsRepository.saveAndFlush(questions);
                            if(questionsDTO.getAnswerOptionDTOList() != null
                                    && !questionsDTO.getAnswerOptionDTOList().isEmpty()) {
                                for(AnswerOptionDTO answerOptionDTO: questionsDTO.getAnswerOptionDTOList()) {
                                    AnswerOption answerOption = new AnswerOption();
                                    answerOption.setOptionDetails(answerOptionDTO.getOptionDetails());
                                    answerOption.setCorrect(answerOptionDTO.isCorrect());
                                    answerOption.setDisplayOrder(answerOptionDTO.getDisplayOrder());
                                    answerOption.setCreatedAt(LocalDateTime.now());
                                    answerOption.setCreatedBy(userAuth.getUserId());
                                    answerOption.setUpdatedAt(null);
                                    answerOption.setActiveFlag(true);
                                    answerOption.setQuestions(questions);

                                    answerOptionList.add(answerOption);
                                }
                            } else {
                                throw new ExamQuestionEmptyException("Please Provide Valid Question/Answers for Exam Creation.");
                            }

                            answerOptionRepository.saveAllAndFlush(answerOptionList);
                        }
                        logger.info("Outside Manual Insert Condition for Exam Creation");
                    } else {
                        throw new ExamQuestionEmptyException("Please Provide " +
                                "Question/Answers for the provided Exam.");
                    }
                } else {
                    throw new UsernameNotFoundException("Cannot Find a valid User For Exam Creation. " +
                            "Please try after sometime");
                }
            } else {
                throw new UsernameNotFoundException("Cannot Find a valid User For Exam Creation. " +
                        "Please try after sometime");
            }
        } catch (ExamQuestionEmptyException examQuestionEmptyException) {
            throw new ExamQuestionEmptyException(examQuestionEmptyException.getMessage());
        } catch(UsernameNotFoundException usernameNotFoundException) {
            throw new UsernameNotFoundException(usernameNotFoundException.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExamDTO> fetchOngoingExams(Integer pageNumber, Integer pageSize) {
        logger.info("Inside FetchOngoingExams method of ExamServiceImpl");
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Exam> examList = examRepository.
                    findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveFlagOrderByStartDateDesc
                            (LocalDateTime.now(), LocalDateTime.now(), true, pageable);
            if(examList != null && !examList.isEmpty()) {
                List<ExamDTO> examDTOList = new ArrayList<>();
                for(Exam exam: examList) {
                    ExamDTO examDTO = getExamDTO(exam);
                    examDTO.setTotalRecords(examList.getTotalElements());
                    examDTOList.add(examDTO);
                }
                logger.info("Outside FetchOngoingExams method of ExamServiceImpl");
                return examDTOList;
            } else {
                logger.info("Outside FetchOngoingExams method of ExamServiceImpl with No Exams Found");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExamDTO> fetchUpcomingExams(Integer pageNumber, Integer pageSize) {
        logger.info("Inside FetchUpcomingExams method of ExamServiceImpl");
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Exam> examList = examRepository.
                    findByStartDateAfterAndActiveFlagOrderByStartDateDesc
                            (LocalDateTime.now(), true, pageable);
            if(examList != null && !examList.isEmpty()) {
                List<ExamDTO> examDTOList = new ArrayList<>();
                for(Exam exam: examList) {
                    ExamDTO examDTO = getExamDTO(exam);
                    examDTO.setTotalRecords(examList.getTotalElements());
                    examDTOList.add(examDTO);
                }
                logger.info("Outside FetchUpcomingExams method of ExamServiceImpl");
                return examDTOList;
            } else {
                logger.info("Outside FetchUpcomingExams method of ExamServiceImpl with No Exams Found");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ExamDTO getExamDTO(Exam exam) {
        ExamDTO examDTO = new ExamDTO();
        examDTO.setExamId(exam.getExamId());
        examDTO.setTitle(exam.getTitle());
        examDTO.setDescription(exam.getDescription());
        examDTO.setDurationInSeconds(exam.getDurationSeconds());
        examDTO.setStatus(exam.getStatus());
        examDTO.setNumberOfQuestion(exam.getNumberOfQuestions());
        examDTO.setStartDateTime(exam.getStartDate());
        examDTO.setEndDateTime(exam.getEndDate());
        examDTO.setSubject(exam.getSubject());
        return examDTO;
    }

    public List<QuestionsDTO> generateQuestionAnswerFromFileBytes(byte[] fileBytes) throws Exception {
        try(InputStream is = new ByteArrayInputStream(fileBytes);
            Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if(!rowIterator.hasNext()) return Collections.emptyList();

            Row rowHeader = rowIterator.next();
            Map<String, Integer> headerMap = new HashMap<>();
            for(Cell cell: rowHeader) {
                String headerText = getCellString(cell).trim();
                headerMap.put(headerText, cell.getColumnIndex());
            }

            List<QuestionsDTO> questionsDTOList = new ArrayList<>();
            int currentRowIndex = 0;
            while(rowIterator.hasNext()) {
                currentRowIndex++;
                Row row = rowIterator.next();
                if(isRowEmpty(row)) continue;

                QuestionsDTO questionsDTO = new QuestionsDTO();
                List<AnswerOptionDTO> answerOptionDTOList = new ArrayList<>();
                if(headerMap.containsKey("Question_Type")) {
                    if(getCellString(row.getCell(headerMap.get("Question_Type")))
                            .equalsIgnoreCase("MULTI_CHOICE")) {
                        questionsDTO.setQuestionType(QuestionType.MCQ_MULTI);
                    } else if(getCellString(row.getCell(headerMap.get("Question_Type")))
                            .equalsIgnoreCase("MCQ_SINGLE")) {
                        questionsDTO.setQuestionType(QuestionType.MCQ_SINGLE);
                    } else if(getCellString(row.getCell(headerMap.get("Question_Type")))
                            .equalsIgnoreCase("MCQ_DESCRIPTIVE")) {
                        questionsDTO.setQuestionType(QuestionType.MCQ_DESCRIPTIVE);
                    } else if(getCellString(row.getCell(headerMap.get("Question_Type")))
                            .equalsIgnoreCase("TRUE_FALSE")) {
                        questionsDTO.setQuestionType(QuestionType.TRUE_FALSE);
                    } else if(getCellString(row.getCell(headerMap.get("Question_Type")))
                            .equalsIgnoreCase("MATCH_COLUMN")) {
                        questionsDTO.setQuestionType(QuestionType.MATCHING);
                    } else if(getCellString(row.getCell(headerMap.get("Question_Type")))
                            .equalsIgnoreCase("FILL_BLANK")) {
                        questionsDTO.setQuestionType(QuestionType.FILL_BLANK);
                    } else {
                        throw new ExamQuestionEmptyException("Please Enter a " +
                                "Valid Question Type at Row Number: " + currentRowIndex);
                    }
                } else {
                    throw new ExamQuestionEmptyException("Excel Format MisMatch.");
                }
                if(headerMap.containsKey("Question_Name")) {
                    String questionName = getCellString(row.getCell(headerMap.get("Question_Name")));
                    if(!questionName.isEmpty()) {
                        questionsDTO.setQuestionText(questionName);
                    } else {
                        throw new ExamQuestionEmptyException("Question Name can't be empty at Row Number: "
                                + currentRowIndex);
                    }
                } else {
                    throw new ExamQuestionEmptyException("Excel Format MisMatch.");
                };
                int count = 0;
                if(headerMap.containsKey("Option_One_(This will be marked as correct answer for this question)")) {
                    String optionOne = getCellString(row.getCell(headerMap.get
                            ("Option_One_(This will be marked as correct answer for this question)")));
                    if(!optionOne.isEmpty()) {
                        count++;
                        AnswerOptionDTO answerOptionDTO = new AnswerOptionDTO();
                        answerOptionDTO.setOptionDetails(optionOne);
                        answerOptionDTO.setCorrect(true);
                        answerOptionDTO.setActiveFlag(true);
                        answerOptionDTO.setCreatedAt(LocalDateTime.now());
                        answerOptionDTO.setDisplayOrder(0);

                        answerOptionDTOList.add(answerOptionDTO);
                    } else {
                        throw new ExamQuestionEmptyException("Please Enter a Valid Option at Row Number: "
                                + currentRowIndex);
                    }
                } else {
                    throw new ExamQuestionEmptyException("Excel Format MisMatch.");
                }
                if(headerMap.containsKey("Option_Two")) {
                    String optionTwo = getCellString(row.getCell(headerMap.get("Option_Two")));
                    if(!optionTwo.isEmpty()) {
                        count++;
                        AnswerOptionDTO answerOptionDTO = new AnswerOptionDTO();
                        answerOptionDTO.setOptionDetails(optionTwo);
                        answerOptionDTO.setCorrect(false);
                        answerOptionDTO.setActiveFlag(true);
                        answerOptionDTO.setCreatedAt(LocalDateTime.now());
                        answerOptionDTO.setDisplayOrder(0);

                        answerOptionDTOList.add(answerOptionDTO);
                    } else {
                        throw new ExamQuestionEmptyException("Please Enter a Valid Option at Row Number: "
                                + currentRowIndex);
                    }
                }
                if(headerMap.containsKey("Option_Three")) {
                    String optionThree = getCellString(row.getCell(headerMap.get("Option_Three")));
                    if(!optionThree.isEmpty()) {
                        count++;
                        AnswerOptionDTO answerOptionDTO = new AnswerOptionDTO();
                        answerOptionDTO.setOptionDetails(optionThree);
                        answerOptionDTO.setCorrect(false);
                        answerOptionDTO.setActiveFlag(true);
                        answerOptionDTO.setCreatedAt(LocalDateTime.now());
                        answerOptionDTO.setDisplayOrder(0);

                        answerOptionDTOList.add(answerOptionDTO);
                    } else {
                        throw new ExamQuestionEmptyException("Please Enter a Valid Option at Row Number: "
                                + currentRowIndex);
                    }
                }
                if(headerMap.containsKey("Option_Four")) {
                    String optionFour = getCellString(row.getCell(headerMap.get("Option_Four")));
                    if(!optionFour.isEmpty()) {
                        count++;
                        AnswerOptionDTO answerOptionDTO = new AnswerOptionDTO();
                        answerOptionDTO.setOptionDetails(optionFour);
                        answerOptionDTO.setCorrect(false);
                        answerOptionDTO.setActiveFlag(true);
                        answerOptionDTO.setCreatedAt(LocalDateTime.now());
                        answerOptionDTO.setDisplayOrder(0);

                        answerOptionDTOList.add(answerOptionDTO);
                    } else {
                        throw new ExamQuestionEmptyException("Please Enter a Valid Option at Row Number: "
                                + currentRowIndex);
                    }
                }
                if(headerMap.containsKey("Option_Five")) {
                    String optionFive = getCellString(row.getCell(headerMap.get("Option_Five")));
                    if(!optionFive.isEmpty()) {
                        count++;
                        AnswerOptionDTO answerOptionDTO = new AnswerOptionDTO();
                        answerOptionDTO.setOptionDetails(optionFive);
                        answerOptionDTO.setCorrect(false);
                        answerOptionDTO.setActiveFlag(true);
                        answerOptionDTO.setCreatedAt(LocalDateTime.now());
                        answerOptionDTO.setDisplayOrder(0);

                        answerOptionDTOList.add(answerOptionDTO);
                    } else {
                        throw new ExamQuestionEmptyException("Please Enter a Valid Option at Row Number: "
                                + currentRowIndex);
                    }
                }
                if(count < 2) {
                    throw new ExamQuestionEmptyException("Please Enter Sufficient amount of options for " +
                            "Question at Row Number: " + currentRowIndex);
                } else {
                    questionsDTO.setAnswerOptionDTOList(answerOptionDTOList);
                }
                if(headerMap.containsKey("Default_Marks")) {
                    if(row.getCell(headerMap.get("Default_Marks")).getCellType().toString().equals("NUMERIC")) {
                        String defaultMarks = getCellString(row.getCell(headerMap.get("Default_Marks")));
                        if(defaultMarks != null && Double.parseDouble(defaultMarks) > 0) {
                            questionsDTO.setDefaultMarks(Double.valueOf(defaultMarks));
                        } else {
                            throw new ExamQuestionEmptyException("Please Enter " +
                                    "valid marks at Row Number: " + currentRowIndex);
                        }
                    } else {
                        throw new ExamQuestionEmptyException("Please Enter " +
                                "valid marks at Row Number: " + currentRowIndex);
                    }
                } else {
                    throw new ExamQuestionEmptyException("Excel Format MisMatch.");
                }
                if(headerMap.containsKey("Negative_Marks")) {
                    if(row.getCell(headerMap.get("Negative_Marks")).getCellType().toString().equals("NUMERIC")) {
                        String negativeMarks = getCellString(row.getCell(headerMap.get("Negative_Marks")));
                        if(negativeMarks != null) {
                            questionsDTO.setDefaultNegativeMarks(Double.valueOf(negativeMarks));
                        } else {
                            throw new ExamQuestionEmptyException("Please Enter " +
                                    "valid marks at Row Number: " + currentRowIndex);
                        }
                    } else {
                        throw new ExamQuestionEmptyException("Please Enter " +
                                "valid marks at Row Number: " + currentRowIndex);
                    }
                } else {
                    throw new ExamQuestionEmptyException("Excel Format MisMatch.");
                }
                if(headerMap.containsKey("Difficulty_Level")) {
                    String difficultyLevel = getCellString(row.getCell(headerMap.get("Difficulty_Level")));
                    if(difficultyLevel != null) {
                        questionsDTO.setDifficulty(difficultyLevel);
                    } else {
                        throw new ExamQuestionEmptyException("Please Enter a Valid Difficulty " +
                                "Level at Row Number: " + currentRowIndex);
                    }
                } else {
                    throw new ExamQuestionEmptyException("Excel Format MisMatch.");
                }
                if(headerMap.containsKey("Subject_Details")) {
                    String subjectDetails = getCellString(row.getCell(headerMap.get("Subject_Details")));
                    if(subjectDetails != null) {
                        questionsDTO.setSubject(subjectDetails);
                    } else {
                        throw new ExamQuestionEmptyException("Please Enter a Valid Subject Details " +
                                "at Row Number: " + currentRowIndex);
                    }
                } else {
                    throw new ExamQuestionEmptyException("Excel Format MisMatch.");
                }
                if(headerMap.containsKey("Question_Explanation")) {
                    String questionExplanation = getCellString(row.getCell(headerMap.get("Question_Explanation")));
                    if(questionExplanation != null) {
                        questionsDTO.setExplanation(questionExplanation);
                    } else {
                        throw new ExamQuestionEmptyException("Please Enter a Valid Explanation " +
                                "at Row Number: " + currentRowIndex);
                    }
                } else {
                    throw new ExamQuestionEmptyException("Excel Format MisMatch.");
                }

                questionsDTOList.add(questionsDTO);
            }

            return questionsDTOList;
        } catch (ExamQuestionEmptyException examQuestionEmptyException) {
            throw new ExamQuestionEmptyException(examQuestionEmptyException.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (Cell c : row) {
            if (c != null && c.getCellType() != CellType.BLANK) {
                String s = getCellString(c);
                if (s != null && !s.isBlank()) return false;
            }
        }
        return true;
    }

    private static String getCellString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                } else {
                    double d = cell.getNumericCellValue();
                    // remove .0 for integers
                    if (d == Math.floor(d)) yield String.valueOf((long) d);
                    else yield String.valueOf(d);
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    CellValue value = evaluator.evaluate(cell);
                    yield cellValueToString(value);
                } catch (Exception e) {
                    yield "";
                }
            }
            default -> "";
        };
    }

    private static String cellValueToString(CellValue v) {
        if (v == null) return "";
        return switch (v.getCellType()) {
            case STRING -> v.getStringValue();
            case NUMERIC -> String.valueOf(v.getNumberValue());
            case BOOLEAN -> String.valueOf(v.getBooleanValue());
            default -> "";
        };
    }
}
