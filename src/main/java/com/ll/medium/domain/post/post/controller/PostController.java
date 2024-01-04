package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.entity.payPost;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq.Rq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final Rq rq;
    private Member member;
    private MemberService memberService;

    @GetMapping("/{id}")
    public String showDetail(@PathVariable long id) {

        if(!rq.isLogin()) {
            return rq.redirect("/member/login", "회원가입과 로그인을 부탁드립니다.");
        }
        //
        // Post post =  postService.findById(id).get();
        // Boolean isPre = post.isPremium();

        // if ( isPre == true ) {
        //  if ( logined == true ) {
        //   Member member;
        //   List<GtrantedAuth> auth = member.getAuthorites(); <-
        //   for ( GtrantedAuth a : auth )
        //   PAID
        // }
        //  else {
        //   redirect ->
        // }
        // }
        // else { <-

        // find post
        // is premium -> ture
        // ture ->
            // login ->
                // true ->
                    // member auth
                        // is -> PAID
                            // show page
                        // is not PAID
                            // error -> this post is premium post.
                // false -> redirect -> login
            // false
                // show-page
        rq.setAttribute("post", postService.findById(id).get());
        return "domain/post/post/detail";
    }

    @GetMapping("/paid/{id}")
    public String payShowDetail(@PathVariable long id) {
        if(!rq.isLogin()) {
            return rq.redirect("/member/login", "회원가입과 로그인을 부탁드립니다.");
        }

        if(!rq.isPaid()) {
            return rq.redirect("/post/list", "유료멤버십 전용입니다.");
        }

        rq.setAttribute("post", postService.findById(id).get());

        return "domain/post/post/paiddetail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/paid/write")
    public String payWrite() {
        if(!rq.isLogin()) {
            return rq.redirect("/member/login", "회원가입과 로그인을 부탁드립니다.");
        }

        if(!rq.isPaid()) {
            return rq.redirect("/post/list", "유료멤버십 전용입니다.");
        }

        return "domain/post/post/paidwrite";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String write() {
        if(!rq.isLogin()) {
            return rq.redirect("/member/login", "회원가입과 로그인을 부탁드립니다.");
        }

        return "domain/post/post/write";
    }

    @Data
    public static class paidwriteForm {
        @NotBlank
        private String title;
        @NotBlank
        private String body;
    }

    @Data
    public static class writeForm {
        @NotBlank
        private String title;
        @NotBlank
        private String body;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/paid/write")
    String payWrite(@Valid paidwriteForm paidwriteForm) {

        postService.payWrite(rq.getMember(), paidwriteForm.title, paidwriteForm.body, true);

        return rq.redirect("/post/paid/list", "게시물 생성되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    String write(@Valid writeForm writeForm) {

        postService.write(rq.getMember(), writeForm.title, writeForm.body, true);

        return rq.redirect("/post/list", "게시물 생성되었습니다.");
    }

    @GetMapping("/list")
    public String showList(
            @RequestParam(defaultValue = "") String kw,
            @RequestParam(defaultValue = "1") int page
    ) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));

        Page<Post> postPage = postService.search(kw, pageable);
        rq.setAttribute("postPage", postPage);
        rq.setAttribute("page", page);

        return "domain/post/post/list";
    }

    @GetMapping("/paid/list")
    public String paidShowList(
            @RequestParam(defaultValue = "") String kw,
            @RequestParam(defaultValue = "1") int page
    ) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));

        Page<payPost> postPage = postService.paySearch(kw, pageable);
        rq.setAttribute("postPage", postPage);
        rq.setAttribute("page", page);

        return "domain/post/post/paidlist";
    }
}
