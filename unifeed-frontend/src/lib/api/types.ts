export type UUID = string;
export type ISODateTime = string;

export interface ValidationError {
  field: string;
  message: string;
}

export interface ApiProblem {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance?: string;
  code?: string;
  timestamp?: ISODateTime;
  validationErrors?: ValidationError[] | null;
}

export interface AuthTokens {
  accessToken: string;
  testAccount: boolean;
}

export interface RegisterRequest { email: string; password: string; displayName: string }
export interface RegisterResponse { userId: UUID }
export interface LoginRequest { email: string; password: string }
export interface VerifyOtpRequest { email: string; otp: string }
export interface ForgotPasswordRequest { email: string }
export interface ResetPasswordRequest { email: string; token: string; newPassword: string }

export interface Profile {
  id: UUID;
  email: string | null;
  displayName: string;
  avatarPath: string | null;
  bio: string | null;
  department: string | null;
  academicYear: string | null;
  globalRole: string;
  testAccount: boolean;
  followerCount: number;
  followingCount: number;
  clubCount: number;
  following: boolean;
}

export interface UpdateProfileRequest {
  displayName?: string | null;
  avatarPath?: string | null;
  bio?: string | null;
  department?: string | null;
  academicYear?: string | null;
}

export interface Post {
  id: UUID;
  authorId: UUID;
  authorName: string;
  authorAvatarPath: string | null;
  content: string;
  likeCount: number;
  commentCount: number;
  createdAt: ISODateTime;
  liked: boolean;
  mediaPaths: string[];
}

export interface UserPost { id: UUID; content: string; likeCount: number; commentCount: number; createdAt: ISODateTime; mediaPaths: string[] }
export interface FeedPage { items: Post[]; nextCursorCreatedAt: ISODateTime | null; nextCursorId: UUID | null }
export interface FeedParams { limit?: number; cursorCreatedAt?: ISODateTime; cursorId?: UUID }
export interface CreatePostRequest { content: string; mediaPaths?: string[] }
export interface IdResponse { id: UUID }
export interface BooleanResponse { liked?: boolean; following?: boolean }
export interface CommentRequest { parentId?: UUID | null; content: string }
export interface Comment { id: UUID; userId: UUID; displayName: string; avatarPath: string | null; parentId: UUID | null; content: string; createdAt: ISODateTime }

export interface Story { id: UUID; authorId: UUID; authorName: string; authorAvatarPath: string | null; mediaPath: string; expiresAt: ISODateTime; viewCount: number; createdAt: ISODateTime }
export interface CreateStoryRequest { mediaPath: string }

export interface ClubSummary { id: UUID; name: string; description: string | null; category: string | null; logoPath: string | null; coverPath: string | null; status: string; memberCount: number; currentUserRole: string | null }
export interface ClubDetail extends ClubSummary { upcomingEventCount: number }
export interface ClubRequest { name: string; description?: string | null; category?: string | null; logoPath?: string | null; coverPath?: string | null }
export interface ClubListParams { query?: string; mine?: boolean; limit?: number }
export interface EventRequest { title: string; description?: string | null; posterPath?: string | null; startsAt: ISODateTime; endsAt?: ISODateTime | null; location?: string | null; category?: string | null }
export interface ClubEvent { id: UUID; clubId: UUID; clubName: string; title: string; description: string | null; posterPath: string | null; startsAt: ISODateTime; endsAt: ISODateTime | null; location: string | null; category: string | null; attending: boolean }
export interface EventListParams { clubId?: UUID; query?: string; limit?: number }
export interface InviteCandidate { id: UUID; displayName: string; avatarPath: string | null }

export interface SearchUser { id: UUID; displayName: string; avatarPath: string | null; department: string | null }
export interface SearchClub { id: UUID; name: string; logoPath: string | null; category: string | null }
export interface SearchEvent { id: UUID; title: string; posterPath: string | null; startsAt: ISODateTime; location: string | null }
export interface SearchResult { users: SearchUser[]; clubs: SearchClub[]; events: SearchEvent[] }

export interface ChatThread { id: UUID; type: string; clubId: UUID | null; title: string | null; lastMessage: string | null; lastMessageAt: ISODateTime | null; unreadCount: number }
export interface ChatMessage { id: UUID; threadId: UUID; senderId: UUID; content: string; sentAt: ISODateTime; readAt: ISODateTime | null }
export interface DirectThreadRequest { participantId: UUID }
export interface SendChatMessage { content: string }
export interface Notification { id: UUID; type: string; payload: Record<string, unknown>; readAt: ISODateTime | null; createdAt: ISODateTime }
export type NotificationFilter = 'ALL' | 'UNREAD' | string;

export type MediaCategory = 'AVATAR' | 'POST' | 'STORY' | 'CLUB';
export interface MediaPaths { thumbnailPath: string; mediumPath: string; originalPath: string }
