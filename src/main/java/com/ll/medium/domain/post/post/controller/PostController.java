package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq.Rq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

        rq.setAttribute("post", postService.findById(id).get());
        return "domain/post/post/detail";
    }

    @GetMapping("/paid/{id}")
    public String payShowDetail(@PathVariable long id) {
        rq.setAttribute("post", postService.findById(id).get());

        return "domain/post/post/paiddetail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/paid/write")
    public String write() {

        return "domain/post/post/paidwrite";
    }

    @Data
    public static class paidwriteForm {
        @NotBlank
        private String title;
        @NotBlank
        private String body;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/paid/write")
    @SneakyThrows
    String write(@Valid paidwriteForm paidwriteForm) {


        postService.write(rq.getMember(), paidwriteForm.title, paidwriteForm.body, true);

        return "domain/post/post/list";
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
}
