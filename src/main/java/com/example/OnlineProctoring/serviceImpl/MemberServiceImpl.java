package com.example.OnlineProctoring.serviceImpl;

import com.example.OnlineProctoring.models.Exam;
import com.example.OnlineProctoring.models.MemberRegistration;
import com.example.OnlineProctoring.models.MemberRegistrationDTO;
import com.example.OnlineProctoring.models.UserAuth;
import com.example.OnlineProctoring.repository.ExamRepository;
import com.example.OnlineProctoring.repository.MemberRegistrationRepository;
import com.example.OnlineProctoring.repository.UserRepository;
import com.example.OnlineProctoring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Override
    public Integer memberRegistration(MemberRegistrationDTO memberRegistrationDTO, String userName) {
        try {
            if(userName != null) {
                UserAuth userAuth = userRepository.findByUserName(userName);
                if(userAuth != null) {
                    if(memberRegistrationDTO.getExamId() != null) {
                        Optional<Exam> exam = examRepository.findById(memberRegistrationDTO.getExamId());
                        if(exam.isPresent()) {
                            MemberRegistration memberRegistration = new MemberRegistration();
                            memberRegistration.setExam(exam.get());
                            memberRegistration.setCoupounId(memberRegistrationDTO.getCoupounId());
                            memberRegistration.setPaymentAmountPaid(memberRegistrationDTO.getPaymentAmountPaid());
                            memberRegistration.setGstAmount(memberRegistrationDTO.getGstAmount());
                            memberRegistration.setPaymentAmountInclusiveGst(memberRegistrationDTO.getPaymentAmountInclusiveGst());
                            memberRegistration.setTransactionId(null);
                            memberRegistration.setUserId(userAuth.getUserId());
                            memberRegistration.setCreatedAt(LocalDateTime.now());

                            memberRegistrationRepository.saveAndFlush(memberRegistration);
                            return 1;
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
}
