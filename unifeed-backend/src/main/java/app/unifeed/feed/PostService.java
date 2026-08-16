package app.unifeed.feed;

import app.unifeed.error.BusinessException;
import app.unifeed.error.ErrorCode;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class PostService {
    private final PostRepository posts;
    PostService(PostRepository posts) { this.posts = posts; }
    @Transactional UUID create(UUID authorId, PostController.CreatePost request) { UUID id=UUID.randomUUID(); posts.insert(id,authorId,request.content()); if(request.mediaPaths()!=null) posts.addMedia(id,request.mediaPaths().subList(0,Math.min(request.mediaPaths().size(),10))); return id; }
    PostController.PostDto get(UUID id, UUID viewerId) { List<PostController.PostDto> rows=posts.findById(id,viewerId); if(rows.isEmpty()) throw new BusinessException(ErrorCode.COMMON_404_NOT_FOUND,HttpStatus.NOT_FOUND); return rows.getFirst(); }
    @Transactional void delete(UUID id, UUID authorId) { if(!posts.softDelete(id,authorId)) throw new BusinessException(ErrorCode.COMMON_403_FORBIDDEN,HttpStatus.FORBIDDEN); }
}
