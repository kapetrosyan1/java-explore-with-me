package ru.practicum.ewm.event.comment.dto.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.event.comment.dto.CommentResultDto;
import ru.practicum.ewm.event.comment.dto.NewCommentDto;
import ru.practicum.ewm.event.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

@Slf4j
public class CommentMapper {
    public static CommentResultDto toCommentResultDto(Comment comment) {
        log.info("CommentMapper: Конвертация Comment в CommentResultDto");
        CommentResultDto commentResultDto = new CommentResultDto();
        commentResultDto.setId(comment.getId());
        commentResultDto.setText(comment.getText());
        commentResultDto.setAuthorName(comment.getAuthor().getName());
        commentResultDto.setCreated(comment.getCreated());
        return commentResultDto;
    }

    public static Comment toComment(NewCommentDto newCommentDto, User author, Event event) {
        log.info("CommentMapper: Конвертация NewCommentDto в Comment");
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setAuthor(author);
        comment.setEvent(event);
        return comment;
    }
}
