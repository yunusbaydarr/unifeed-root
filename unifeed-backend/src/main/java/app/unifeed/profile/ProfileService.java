package app.unifeed.profile;

import app.unifeed.error.BusinessException;
import app.unifeed.error.ErrorCode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final JdbcTemplate jdbc;
    public ProfileService(JdbcTemplate jdbc){this.jdbc=jdbc;}
    public ProfileDto get(UUID requested,UUID viewer){
        ProfileDto profile=jdbc.query("""
            SELECT u.id,u.email,u.display_name,u.avatar_path,u.bio,u.department,u.academic_year,u.global_role::text,u.is_test_account,
              (SELECT count(*) FROM follows WHERE following_id=u.id),(SELECT count(*) FROM follows WHERE follower_id=u.id),
              (SELECT count(*) FROM club_members WHERE user_id=u.id),EXISTS(SELECT 1 FROM follows WHERE follower_id=? AND following_id=u.id)
            FROM users u WHERE u.id=? AND u.deleted_at IS NULL
            """,(rs,n)->new ProfileDto((UUID)rs.getObject(1),viewer.equals(requested)?rs.getString(2):null,rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),viewer.equals(requested)&&rs.getBoolean(9),rs.getLong(10),rs.getLong(11),rs.getLong(12),rs.getBoolean(13)),viewer,requested).stream().findFirst().orElse(null);
        if(profile==null)throw new BusinessException(ErrorCode.COMMON_404_NOT_FOUND,HttpStatus.NOT_FOUND); return profile;
    }
    @Transactional public ProfileDto update(UUID user,UpdateProfileRequest r){jdbc.update("UPDATE users SET display_name=COALESCE(?,display_name),avatar_path=COALESCE(?,avatar_path),bio=COALESCE(?,bio),department=COALESCE(?,department),academic_year=COALESCE(?,academic_year) WHERE id=? AND deleted_at IS NULL",r.displayName(),r.avatarPath(),r.bio(),r.department(),r.academicYear(),user);return get(user,user);}
    public List<UserPostDto> posts(UUID user,int limit){return jdbc.query("""
        SELECT p.id,p.content,p.like_count,p.comment_count,p.created_at,COALESCE(array_agg(pm.media_path ORDER BY pm.order_index) FILTER (WHERE pm.id IS NOT NULL),ARRAY[]::text[])
        FROM posts p LEFT JOIN post_media pm ON pm.post_id=p.id AND pm.deleted_at IS NULL
        WHERE p.author_id=? AND p.deleted_at IS NULL GROUP BY p.id ORDER BY p.created_at DESC LIMIT ?
        """,(rs,n)->new UserPostDto((UUID)rs.getObject(1),rs.getString(2),rs.getInt(3),rs.getInt(4),rs.getTimestamp(5).toInstant(),List.of((String[])rs.getArray(6).getArray())),user,Math.min(Math.max(limit,1),50));}
    public List<ProfileListItemDto> followers(UUID user,int limit){return profileList("SELECT u.id,u.display_name,u.avatar_path,u.department FROM follows f JOIN users u ON u.id=f.follower_id WHERE f.following_id=? AND u.deleted_at IS NULL ORDER BY u.display_name LIMIT ?",user,limit);}
    public List<ProfileListItemDto> following(UUID user,int limit){return profileList("SELECT u.id,u.display_name,u.avatar_path,u.department FROM follows f JOIN users u ON u.id=f.following_id WHERE f.follower_id=? AND u.deleted_at IS NULL ORDER BY u.display_name LIMIT ?",user,limit);}
    private List<ProfileListItemDto> profileList(String sql,UUID user,int limit){return jdbc.query(sql,(rs,n)->new ProfileListItemDto((UUID)rs.getObject(1),rs.getString(2),rs.getString(3),rs.getString(4)),user,Math.min(Math.max(limit,1),100));}
    public List<MemberClubDto> clubs(UUID user,int limit){return jdbc.query("""
        SELECT c.id,c.name,c.logo_path,c.category,count(all_members.user_id)
        FROM club_members membership JOIN clubs c ON c.id=membership.club_id LEFT JOIN club_members all_members ON all_members.club_id=c.id
        WHERE membership.user_id=? AND c.deleted_at IS NULL GROUP BY c.id ORDER BY c.name LIMIT ?
        """,(rs,n)->new MemberClubDto((UUID)rs.getObject(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getLong(5)),user,Math.min(Math.max(limit,1),100));}
    public record ProfileDto(UUID id,String email,String displayName,String avatarPath,String bio,String department,String academicYear,String globalRole,boolean testAccount,long followerCount,long followingCount,long clubCount,boolean following){}
    public record UpdateProfileRequest(String displayName,String avatarPath,String bio,String department,String academicYear){}
    public record UserPostDto(UUID id,String content,int likeCount,int commentCount,Instant createdAt,List<String> mediaPaths){}
    public record ProfileListItemDto(UUID id,String displayName,String avatarPath,String department){}
    public record MemberClubDto(UUID id,String name,String logoPath,String category,long memberCount){}
}
