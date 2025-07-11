package com.example.myShop.controller;

import com.example.myShop.dto.MemberFormDto;
import com.example.myShop.entity.Member;
import com.example.myShop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입 폼 출력
    @GetMapping("/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

//    // 회원 가입 처리
//    @PostMapping("/new")
//    public String memberForm(MemberFormDto memberFormDto) {
//        Member member = Member.createMember(memberFormDto, passwordEncoder);
//        memberService.saveMember(member);
//        return "redirect:/";
//    }

    @PostMapping("/new")
    public String newMember(@Valid MemberFormDto memberFormDto,
                            BindingResult bindingResult,
                            Model model) {

        // 유효성 검사 실패 시 다시 회원가입 폼으로 이동
        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        // 회원가입 성공 시 홈으로 리다이렉트
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("memberLoginFormDto", new MemberFormDto());
        return "member/memberLoginForm";
    }


    @GetMapping("/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/memberLoginForm";
    }
}
