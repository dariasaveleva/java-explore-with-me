package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentDtoCreate;
import ru.practicum.ewm.comments.dto.CommentDtoUpdate;
import ru.practicum.ewm.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/comments")
public class CommentController {
    private final CommentService service;

    @GetMapping()
    public List<CommentDto> findAllCommentsForEvent(@RequestParam Long eventId,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                    @PositiveOrZero @RequestParam(defaultValue = "10") int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return service.findAllCommentsForEvent(eventId, page);
    }

    @GetMapping("/{commentId}")
    public CommentDto findCommentById(@PathVariable Long commentId) {
        return service.getCommentById(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable Long commentId,
                                    @RequestParam Long userId) {
         service.deleteComment(commentId,userId);
    }


    @DeleteMapping("/admin/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        service.deleteCommentByAdmin(commentId);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@Valid @RequestBody CommentDtoCreate commentDtoCreate,
                                    @RequestParam Long eventId,
                                    @RequestParam Long userId) {
        return service.addComment(commentDtoCreate, eventId, userId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto editComment(@PathVariable Long commentId,
                                  @RequestParam Long userId,
                                  @Valid @RequestBody CommentDtoUpdate commentDtoUpdate) {
        return service.updateComment(commentId, userId, commentDtoUpdate);
    }
}
