package com.example.OnlineProctoring.serviceImpl;

import com.example.OnlineProctoring.customExceptions.MemberDetailsNotFoundException;
import com.example.OnlineProctoring.models.*;
import com.example.OnlineProctoring.repository.*;
import com.example.OnlineProctoring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private MemberRegistrationRepository memberRegistrationRepository;

    @Autowired
    private MemberExamAttemptRepository memberExamAttemptRepository;

    @Autowired
    private MemberExamQuestionRepository memberExamQuestionRepository;

    @Autowired
    private MemberExamQuestionAnswerRepository memberExamQuestionAnswerRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Value("${running.environment}")
    private String environment;

    @Override
    public Integer memberRegistration(MemberRegistrationDTO memberRegistrationDTO, String userName) {
        try {
            if(userName != null) {
                UserAuth userAuth = userRepository.findByUserName(userName);
                if(userAuth != null) {
                    if(memberRegistrationDTO.getExamId() != null) {
                        Optional<Exam> exam = examRepository.
                                findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveFlagAndExamId
                                        (LocalDateTime.now(), LocalDateTime.now(), true,
                                                memberRegistrationDTO.getExamId());
                        if(exam.isPresent()) {
                            MemberRegistration memberRegistrationOptional = memberRegistrationRepository.
                                    findByExamExamIdAndUserIdAndActiveFlag(exam.get().getExamId(),
                                            userAuth.getUserId(), true);
                            if(memberRegistrationOptional == null) {
                                MemberRegistration memberRegistration = new MemberRegistration();
                                memberRegistration.setExam(exam.get());
                                memberRegistration.setCoupounId(memberRegistrationDTO.getCoupounId());
                                memberRegistration.setPaymentAmountPaid(memberRegistrationDTO.getPaymentAmountPaid());
                                memberRegistration.setGstAmount(memberRegistrationDTO.getGstAmount());
                                memberRegistration.setPaymentAmountInclusiveGst(memberRegistrationDTO.getPaymentAmountInclusiveGst());
                                memberRegistration.setTransactionId(null);
                                memberRegistration.setUserId(userAuth.getUserId());
                                memberRegistration.setCreatedAt(LocalDateTime.now());
                                memberRegistration.setActiveFlag(true);

                                memberRegistrationRepository.saveAndFlush(memberRegistration);
                                return 1;
                            } else {
                                return -1;
                            }
                        } else {
                            return 0;
                        }
                    } else {
                        throw new RuntimeException("Please pass a Valid Exam Id");
                    }
                } else {
                    throw new UsernameNotFoundException("Cannot Find a Valid User For Exam Registration." +
                            "Please try after sometime");
                }
            } else {
                throw new UsernameNotFoundException("Cannot Find a Valid User For Exam Registration." +
                        "Please try after sometime");
            }
        } catch(UsernameNotFoundException usernameNotFoundException) {
          throw new UsernameNotFoundException(usernameNotFoundException.getMessage());
        } catch (RuntimeException runtimeException) {
            throw new RuntimeException(runtimeException.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MemberExamAttemptDTO startExam(MemberExamAttemptDTO memberExamAttemptDTO, String userName) {
        try {
            if(userName != null) {
                UserAuth userAuth = userRepository.findByUserName(userName);
                if(userAuth != null) {
                    MemberRegistration memberRegistration = memberRegistrationRepository.
                            findByExamExamIdAndUserIdAndActiveFlag(memberExamAttemptDTO.getExamId(), userAuth.getUserId(), true);
                    if(memberRegistration != null) {
                        Optional<MemberExamAttempt> memberExamAttemptOptional = memberExamAttemptRepository.
                                findByExamExamIdAndMemberRegistrationMemberRegistrationIdAndActiveFlag
                                        (memberExamAttemptDTO.getExamId(), memberRegistration.getMemberRegistrationId(), true);
                        if(memberExamAttemptOptional.isEmpty()) {
                            Optional<Exam> optionalExam = examRepository.
                                    findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveFlagAndExamId
                                            (LocalDateTime.now(), LocalDateTime.now(), true,
                                                    memberExamAttemptDTO.getExamId());
                            if(optionalExam.isPresent()) {
                                MemberExamAttempt memberExamAttempt = new MemberExamAttempt();
                                memberExamAttempt.setExam(optionalExam.get());
                                memberExamAttempt.setMemberRegistration(memberRegistration);
                                memberExamAttempt.setStartedAt(LocalDateTime.now());
                                memberExamAttempt.setCreatedAt(LocalDateTime.now());
                                long durationInSeconds = Long.parseLong(optionalExam.get().getDurationSeconds());
                                memberExamAttempt.setEndsAt(LocalDateTime.now().plusSeconds(durationInSeconds));
                                memberExamAttempt.setAttemptStatus("STARTED");
                                memberExamAttempt.setActiveFlag(true);
                                memberExamAttemptRepository.saveAndFlush(memberExamAttempt);

                                List<Questions> questionsList = optionalExam.get().getQuestions();
                                List<Questions> shuffeledQuestionsList = new ArrayList<>(questionsList);

                                int questionCount = optionalExam.get().getNumberOfQuestions() != null
                                        ? optionalExam.get().getNumberOfQuestions() : 0;
                                if(questionCount <= 0 || questionCount > shuffeledQuestionsList.size()) {
                                    questionCount = shuffeledQuestionsList.size();
                                }
                                Collections.shuffle(shuffeledQuestionsList);
                                List<Questions> selectedShuffeledQuestionList = shuffeledQuestionsList.
                                        subList(0, questionCount);

                                int sequence = 1;

                                List<MemberExamQuestion> memberExamQuestionList = new ArrayList<>();
                                for(Questions questions: selectedShuffeledQuestionList) {
                                    MemberExamQuestion memberExamQuestion = new MemberExamQuestion();
                                    memberExamQuestion.setMemberExamAttempt(memberExamAttempt);
                                    memberExamQuestion.setAttemptedTime(null);
                                    memberExamQuestion.setQuestions(questions);
                                    memberExamQuestion.setSequenceNumber(sequence++);
                                    memberExamQuestion.setActiveFlag(true);
                                    memberExamQuestion.setCreatedAt(LocalDateTime.now());

                                    memberExamQuestionList.add(memberExamQuestion);
                                }
                                memberExamQuestionRepository.saveAllAndFlush(memberExamQuestionList);

                                MemberExamAttemptDTO memberExamAttemptDTO1 = new MemberExamAttemptDTO();
                                memberExamAttemptDTO1.setAttemptId(memberExamAttempt.getAttemptId());
                                memberExamAttemptDTO1.setExamId(memberExamAttemptDTO.getExamId());
                                return memberExamAttemptDTO1;
                            } else {
                                return null;
                            }
                        } else {
                            if(memberExamAttemptOptional.get().getAttemptStatus().equalsIgnoreCase("EXPIRED")
                                    || (memberExamAttemptOptional.get().getEndsAt().isBefore(LocalDateTime.now())
                                    && !environment.equalsIgnoreCase("DEV"))) {
                                MemberExamAttempt memberExamAttempt = memberExamAttemptOptional.get();
                                memberExamAttempt.setAttemptStatus("EXPIRED");
                                memberExamAttemptRepository.saveAndFlush(memberExamAttempt);
                                return memberExamAttemptDTO;
                            } else {
                                MemberExamAttemptDTO memberExamAttemptDTO1 = new MemberExamAttemptDTO();
                                memberExamAttemptDTO1.setExamId(memberExamAttemptDTO.getExamId());
                                memberExamAttemptDTO1.setAttemptId(memberExamAttemptOptional.get().getAttemptId());
                                return memberExamAttemptDTO1;
                            }
                        }
                    } else {
                        throw new MemberDetailsNotFoundException("You have not registered for the selected exam" +
                                ". Please register for it first.");
                    }
                } else {
                    throw new UsernameNotFoundException("Cannot Find a Valid User For Exam Registration." +
                            "Please try after sometime");
                }
            } else {
                throw new UsernameNotFoundException("Cannot Find a Valid User For Exam Registration." +
                        "Please try after sometime");
            }
        } catch(MemberDetailsNotFoundException memberDetailsNotFoundException) {
            throw new MemberDetailsNotFoundException(memberDetailsNotFoundException.getMessage());
        } catch(UsernameNotFoundException usernameNotFoundException) {
            throw new UsernameNotFoundException(usernameNotFoundException.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MemberExamAttemptDTO> fetchExamQuestionList(MemberExamAttemptDTO memberExamAttemptDTO, Integer pageNumber, Integer pageSize) {
        try {
            if(memberExamAttemptDTO.getAttemptId() != null) {
                Optional<MemberExamAttempt> memberExamAttempt = memberExamAttemptRepository.
                        findByAttemptIdAndActiveFlagAndAttemptStatus
                                (memberExamAttemptDTO.getAttemptId(), true, "STARTED");
                if(memberExamAttempt.isPresent() && (memberExamAttempt.get().getEndsAt().isAfter(LocalDateTime.now())
                        || environment.equalsIgnoreCase("DEV"))) {
                    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("sequenceNumber"));
                    Page<MemberExamQuestion> memberExamQuestions = memberExamQuestionRepository.
                            findByMemberExamAttemptAttemptIdAndActiveFlag(memberExamAttemptDTO.getAttemptId(), true, pageable);
                    List<MemberExamAttemptDTO> memberExamAttemptDTOList = new ArrayList<>();
                    for(MemberExamQuestion memberExamQuestion: memberExamQuestions) {
                        MemberExamAttemptDTO memberExamAttemptDTO1 = new MemberExamAttemptDTO();
                        memberExamAttemptDTO1.setAttemptId(memberExamAttemptDTO.getAttemptId());;
                        memberExamAttemptDTO1.setExamId(memberExamAttempt.get().getExam().getExamId());
                        memberExamAttemptDTO1.setQuestionId(memberExamQuestion.getQuestions().getQuestionId());
                        memberExamAttemptDTO1.setQuestionName(memberExamQuestion.getQuestions().getQuestionText());
                        memberExamAttemptDTO1.setQuestionType(memberExamQuestion.getQuestions().getQuestionType());
                        memberExamAttemptDTO1.setMemberExamQuestionId(memberExamQuestion.getMemQuestionId());
                        List<MemberExamQuestionAnswer> memberExamQuestionAnswerList =
                                memberExamQuestionAnswerRepository.
                                        findByMemberExamAttemptAttemptIdAndMemberExamQuestionMemQuestionIdAndActiveFlag
                                                (memberExamAttemptDTO.getAttemptId(),
                                                        memberExamQuestion.getMemQuestionId(), true);
                        if(memberExamQuestionAnswerList != null && !memberExamQuestionAnswerList.isEmpty()) {
                            List<MemberExamQuestionAnswerDTO> memberExamQuestionAnswerDTOList =
                                    memberExamQuestionAnswerList.stream().map(selectedOptionDetails -> {
                                        MemberExamQuestionAnswerDTO memberExamQuestionAnswerDTO =
                                                new MemberExamQuestionAnswerDTO();
                                        memberExamQuestionAnswerDTO.
                                                setMemQuestionId(
                                                        selectedOptionDetails.getMemberExamQuestion().getMemQuestionId());
                                        memberExamQuestionAnswerDTO.
                                                setAttemptId(selectedOptionDetails.getMemberExamAttempt().getAttemptId());
                                        memberExamQuestionAnswerDTO.
                                                setMemberExamQuesAnsId(selectedOptionDetails.getMemberExamQuesAnsId());
                                        memberExamQuestionAnswerDTO.setSelectedOptionId(
                                                selectedOptionDetails.getAnswerOption().getOptionId());
                                        return memberExamQuestionAnswerDTO;
                                    }).toList();
                            memberExamAttemptDTO1.setMemberExamQuestionAnswerList(memberExamQuestionAnswerDTOList);
                        } else {
                            memberExamAttemptDTO1.setMemberExamQuestionAnswerList(new ArrayList<>());
                        }
                        List<MemberExamAttemptOptionDTO> answerOptionDTOList;
                        List<AnswerOption> answerOptionList =
                                new ArrayList<>(memberExamQuestion.getQuestions().getAnswerOptions());
                        Collections.shuffle(answerOptionList);
                        answerOptionDTOList = answerOptionList.stream().map
                                (answerOption -> {
                                    MemberExamAttemptOptionDTO answerOptionDto = new MemberExamAttemptOptionDTO();
                                    answerOptionDto.setOptionId(answerOption.getOptionId());
                                    answerOptionDto.setOptionDetails(answerOption.getOptionDetails());
                                    return answerOptionDto;
                                }).toList();
                        memberExamAttemptDTO1.setAnswerOptionDTOList(answerOptionDTOList);
                        memberExamAttemptDTO1.setTotalElements(memberExamQuestions.getTotalElements());
                        memberExamAttemptDTOList.add(memberExamAttemptDTO1);
                    }
                    return memberExamAttemptDTOList;
                } else {
                    throw new MemberDetailsNotFoundException("Time is over for the selected Attempt !!");
                }
            } else {
                throw new MemberDetailsNotFoundException("Attempt Details not Found !!");
            }
        } catch (MemberDetailsNotFoundException memberDetailsNotFoundException) {
            throw new MemberDetailsNotFoundException(memberDetailsNotFoundException.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer saveAnswer(MemberExamAttemptDTO memberExamAttemptDTO) {
        try {
            if(memberExamAttemptDTO.getAttemptId() != null) {
                Optional<MemberExamAttempt> memberExamAttempt = memberExamAttemptRepository.
                        findByAttemptIdAndActiveFlagAndAttemptStatus
                                (memberExamAttemptDTO.getAttemptId(), true, "STARTED");
                if(memberExamAttempt.isPresent() && (memberExamAttempt.get().getEndsAt().isAfter(LocalDateTime.now())
                        || environment.equalsIgnoreCase("DEV"))) {
                    Optional<MemberExamQuestion> memberExamQuestion = memberExamQuestionRepository.
                            findById(memberExamAttemptDTO.getQuestionId());
                    if(memberExamQuestion.isPresent()) {
                        List<MemberExamQuestionAnswer> memberExamQuestionAnswerList = new ArrayList<>();
                        for(MemberExamAttemptOptionDTO memberExamAttemptOptionDTO: memberExamAttemptDTO.getAnswerOptionDTOList()) {
                            if(memberExamAttemptOptionDTO.getOptionId() != null) {
                                Optional<AnswerOption> answerOption = answerOptionRepository.findById
                                        (memberExamAttemptOptionDTO.getOptionId());
                                if(answerOption.isPresent()) {
                                    MemberExamQuestionAnswer memberExamQuestionAnswer = new MemberExamQuestionAnswer();
                                    memberExamQuestionAnswer.setAnswerOption(answerOption.get());
                                    memberExamQuestionAnswer.setMemberExamAttempt(memberExamAttempt.get());
                                    memberExamQuestionAnswer.setMemberExamQuestion(memberExamQuestion.get());
                                    memberExamQuestionAnswer.setActiveFlag(true);
                                    memberExamQuestionAnswer.setCreatedAt(LocalDateTime.now());
                                    memberExamQuestionAnswer.setCorrect(answerOption.get().isCorrect());

                                    memberExamQuestionAnswerList.add(memberExamQuestionAnswer);
                                } else {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                        }

                        memberExamQuestionAnswerRepository.saveAllAndFlush(memberExamQuestionAnswerList);
                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    throw new MemberDetailsNotFoundException("Attempt Details not Found !!");
                }
            } else {
                throw new MemberDetailsNotFoundException("Attempt Details not Found !!");
            }
        } catch (MemberDetailsNotFoundException memberDetailsNotFoundException) {
            throw new MemberDetailsNotFoundException(memberDetailsNotFoundException.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
