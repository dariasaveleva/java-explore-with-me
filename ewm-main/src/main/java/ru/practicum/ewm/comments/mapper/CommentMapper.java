package ru.practicum.ewm.comments.mapper;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentDtoCreate;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.model.User;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, Event event) {
        return new Comment(
                null,
                commentDto.getCreated(),
                event,
                commentDto.getAuthor(),
                commentDto.getText()
        );
    }

    public static Comment toCommentFromCommentDtoCreate(CommentDtoCreate commentDtoCreate, Event event, User author) {
        return new Comment(
                null,
                commentDtoCreate.getCreated(),
                event,
                author,
                commentDtoCreate.getText()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getCreated(),
                comment.getEvent().getId(),
                comment.getAuthor(),
                comment.getText()
        );
    }
}
