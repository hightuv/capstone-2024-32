package com.example.WebOrder.controller;

import com.example.WebOrder.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
public class OrderController {

    private final ItemService itemService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;
    private final ProfileService profileService;

    public OrderController(ItemService itemService, OrderService orderService, CategoryService categoryService, ReviewService reviewService, ProfileService profileService) {
        this.itemService = itemService;
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
        this.profileService = profileService;
    }


    // 인증을 성공했을 시 접근가능한 page
    @GetMapping("/order/{userId}/{seatId}")
    public String getShopPageByGuest(@PathVariable Long userId, @PathVariable Long seatId, Model model){
        // 인증 과정 했다 치고
        model.addAttribute("categories", categoryService.getAllCategory(userId));
        model.addAttribute("items",itemService.getAllItemsOfUser(userId));
        model.addAttribute("profile", profileService.getUserProfileById(userId));
        return "order/orderForm";
    }

    //주문하기
    @ResponseBody
    @PostMapping("/order/{userId}/{seatId}")
    public Boolean order(@PathVariable Long userId, @PathVariable Long seatId, @RequestBody String json, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        Long orderId = orderService.order(seatId, json);
        log.info("주문 성공");
        response.addCookie(reviewService.getCookieOfOrderInfo(request, orderId));
        return true;
    }

    @GetMapping("/order/success")
    public String orderSuccess(){
        log.info("주문 성공 리다이렉트");
        return "order/orderSuccess";
    }

}
