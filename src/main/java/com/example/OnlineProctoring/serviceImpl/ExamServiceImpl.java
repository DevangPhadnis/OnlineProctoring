package com.example.OnlineProctoring.serviceImpl;

import com.example.OnlineProctoring.customExceptions.ExamQuestionEmptyException;
import com.example.OnlineProctoring.models.*;
import com.example.OnlineProctoring.repository.AnswerOptionRepository;
import com.example.OnlineProctoring.repository.ExamRepository;
import com.example.OnlineProctoring.repository.QuestionsRepository;
import com.example.OnlineProctoring.repository.UserRepository;
import com.example.OnlineProctoring.service.ExamService;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public Long createExam(ExamDTO examDTO, String userName) throws Exception {
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
                    exam.setActiveFlag(true);
                    exam.setCreatedAt(LocalDateTime.now());
                    exam.setUpdatedAt(null);
                    exam.setStartDate(examDTO.getStartDateTime());
                    exam.setEndDate(examDTO.getEndDateTime());

                    if(examDTO.getQuestionAnswerAttachment() != null) {

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
        return 0L;
    }

    public List<?> generateQuestionAnswerFromFileBytes(String base64FileBytes) throws Exception {
        byte[] fileBytes;
        try {
            fileBytes = Base64.getDecoder().decode(base64FileBytes);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new RuntimeException(illegalArgumentException);
        }

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
            int currentRowIndex = 1;
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if(isRowEmpty(row)) continue;

                QuestionsDTO questionsDTO = new QuestionsDTO();
                List<AnswerOptionDTO> answerOptionDTOList = new ArrayList<>();
                if(headerMap.containsKey("Question_Type")) {
                    if(getCellString(row.getCell(headerMap.get("Question_Type")))
                            .equalsIgnoreCase("MULTI_CHOICE")) {
                        questionsDTO.setQuestionType(QuestionType.MCQ_MULTI);
                    } else if(getCellString(row.getCell(headerMap.get("Question_Type")))
                            .equalsIgnoreCase("SINGLE_CHOICE")) {
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
                } else {
                    throw new ExamQuestionEmptyException("Excel Format MisMatch.");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Collections.emptyList();
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
