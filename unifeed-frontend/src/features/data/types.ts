export interface Profile { id:string; email?:string|null; displayName:string; avatarPath?:string|null; bio?:string|null; department?:string|null; academicYear?:string|null; globalRole:string; testAccount:boolean; followerCount:number; followingCount:number; clubCount:number; following:boolean }
export interface ProfileListItem { id:string; displayName:string; avatarPath?:string|null; department?:string|null }
export interface MemberClub { id:string; name:string; logoPath?:string|null; category?:string|null; memberCount:number }
export interface Post { id:string; authorId:string; authorName:string; authorAvatarPath?:string|null; content:string; likeCount:number; commentCount:number; createdAt:string; liked:boolean; mediaPaths:string[] }
export interface FeedPage { items:Post[]; nextCursorCreatedAt?:string|null; nextCursorId?:string|null }
export interface Story { id:string; authorId:string; authorName:string; authorAvatarPath?:string|null; mediaPath:string; expiresAt:string; viewCount:number; createdAt:string }
export interface Comment { id:string; userId:string; displayName:string; avatarPath?:string|null; parentId?:string|null; content:string; createdAt:string }
export interface Club { id:string; name:string; description?:string|null; category?:string|null; logoPath?:string|null; coverPath?:string|null; status:string; memberCount:number; upcomingEventCount?:number; currentUserRole?:string|null }
export interface EventItem { id:string; clubId:string; clubName:string; title:string; description?:string|null; posterPath?:string|null; startsAt:string; endsAt?:string|null; location?:string|null; category?:string|null; attending:boolean }
export interface NotificationItem { id:string; type:string; payload:Record<string,unknown>; readAt?:string|null; createdAt:string }
export interface ChatThread { id:string; type:string; clubId?:string|null; title:string; lastMessage?:string|null; lastMessageAt?:string|null; unreadCount:number }
export interface ChatMessage { id:string; threadId:string; senderId:string; content:string; sentAt:string; readAt?:string|null }
export interface SearchResult { users:Array<Pick<Profile,'id'|'displayName'|'avatarPath'|'department'>>; clubs:Array<Pick<Club,'id'|'name'|'logoPath'|'category'>>; events:Array<Pick<EventItem,'id'|'title'|'posterPath'|'startsAt'|'location'>> }
export interface MediaPaths { thumbnailPath:string; mediumPath:string; originalPath:string }
export const mediaUrl=(path?:string|null)=>path?`${import.meta.env.VITE_API_BASE_URL??''}${path}`:undefined;
