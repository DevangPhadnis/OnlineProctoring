package com.example.OnlineProctoring.serviceImpl;

import com.example.OnlineProctoring.customExceptions.MemberDetailsNotFoundException;
import com.example.OnlineProctoring.models.*;
import com.example.OnlineProctoring.repository.*;
import com.example.OnlineProctoring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
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
                                    || memberExamAttemptOptional.get().getEndsAt().isBefore(LocalDateTime.now())) {
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
}
