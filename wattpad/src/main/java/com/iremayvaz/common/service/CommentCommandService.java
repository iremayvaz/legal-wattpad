package com.iremayvaz.common.service;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.auth.repository.UserRepository;
import com.iremayvaz.common.model.dto.request.CommentRequest;
import com.iremayvaz.common.model.entity.Comment;
import com.iremayvaz.common.model.entity.CommentReaction;
import com.iremayvaz.common.model.enums.ReactionType;
import com.iremayvaz.common.model.mapper.CommentMapper;
import com.iremayvaz.common.repository.CommentReactionRepository;
import com.iremayvaz.common.repository.CommentRepository;
import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.repository.ChapterRepository;
import com.iremayvaz.content.repository.StoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentCommandService {
    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;

    private final CommentMapper commentMapper;

    @Transactional
    public void postComment(Long storyId, Long chapterId, CommentRequest req) {
        User author = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new EntityNotFoundException("Story not found"));

        Comment comment = commentMapper.toEntity(req);
        comment.setAuthor(author);
        comment.setStory(story);
        comment.setContent(req.getContent());
        comment.setSpoiler(req.isSpoiler());

        // Eğer bir bölüme yorum yapılıyorsa
        if (chapterId != null) {
            Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();
            comment.setChapter(chapter);
        }

        // Eğer bu bir yanıtsa (Reply)
        if (req.getParentId() != null) {
            Comment parent = commentRepository.findById(req.getParentId()).orElseThrow();
            parent.addReply(comment); // Comment entity'ndeki yardımcı metodu kullanıyoruz
        }

        commentRepository.save(comment);
    }

    @Transactional
    public void reactToComment(Long commentId, Long userId, ReactionType type) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        User user = userRepository.getReferenceById(userId);

        Optional<CommentReaction> existing = commentReactionRepository.findByCommentIdAndUserId(commentId, userId);

        if (existing.isPresent()) {
            CommentReaction reaction = existing.get();
            if (reaction.getType() == type) {
                // Aynı tepki -> Tepkiyi geri çek (Remove)
                updateCommentCounts(comment, type, -1);
                commentReactionRepository.delete(reaction);
            } else {
                // Farklı tepki -> Tepkiyi değiştir (Change)
                updateCommentCounts(comment, reaction.getType(), -1);
                updateCommentCounts(comment, type, 1);
                reaction.setType(type);
                commentReactionRepository.save(reaction);
            }
        } else {
            // Yeni tepki -> Ekle
            CommentReaction newReaction = new CommentReaction();
            newReaction.setComment(comment);
            newReaction.setUser(user);
            newReaction.setType(type);
            commentReactionRepository.save(newReaction);
            updateCommentCounts(comment, type, 1);
        }
        commentRepository.save(comment);
    }

    private void updateCommentCounts(Comment comment, ReactionType type, int delta) {
        if (type == ReactionType.LIKE) {
            comment.setLikeCount(comment.getLikeCount() + delta);
        } else {
            comment.setDislikeCount(comment.getDislikeCount() + delta);
        }
    }
}
