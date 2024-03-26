package com.example.WebOrder.controller;

import com.example.WebOrder.dto.ProfileDto;
import com.example.WebOrder.dto.UserEditFormDto;
import com.example.WebOrder.service.LoginService;
import com.example.WebOrder.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class UserProfileController {
    private final LoginService loginService;
    private final ProfileService profileService;

    public UserProfileController(LoginService loginService, ProfileService profileService) {
        this.loginService = loginService;
        this.profileService = profileService;
    }


    // 내 프로필 보기
    @GetMapping("/myprofile")
    public String getMyProfile(Model model){
        ProfileDto dto = profileService.getMyProfile();
        model.addAttribute("profile", dto);
        // 타인의 프로필을 열람할 때와 같은 html을 쓰는데, 이 메소드의 경우 내 프로필을 보는 것이기 때문에 '프로필 수정' 과 같은 버튼이 등장해야 한다.
        // 이것을 구분하기 위해 isMine attribute를 추가하였다.
        model.addAttribute("isMine", true);
        return "html/profile";
    }

    @GetMapping("/myprofile/edit")
    public String getEditMyProfileForm(Model model){
        ProfileDto dto = profileService.getMyProfile();
        model.addAttribute("profile", dto);

        return "html/profileEdit";
    }

    @PostMapping("/myprofile/edit")
    public String editMyProfile(UserEditFormDto dto){

        if (!loginService.isPasswordCorrect(dto.getCurrentPassword())) return "redirect:/myprofile/edit?unauthorized=true";

        if (dto.getChangedPassword().length() < 8 || dto.getChangedPassword().length() > 16) return "redirect:/myprofile/edit?pwderror=true";

        profileService.editMyProfile(dto);
        return "redirect:/myprofile";
    }


    // Username를 통해 타인의 프로필을 가져오기
    @GetMapping("/profile/{username}")
    public String getProfileOfUser(@PathVariable("username") String username, Model model){
        ProfileDto dto = profileService.getUserProfileByUsername(username);
        model.addAttribute("profile", dto);
        model.addAttribute("isMine", false);
        return "html/profile";
    }

}
