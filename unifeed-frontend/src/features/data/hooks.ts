import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { apiClient as api } from '@/lib/api';
import type { ChatMessage, ChatThread, Club, Comment, EventItem, FeedPage, MediaPaths, MemberClub, NotificationItem, Profile, ProfileListItem, SearchResult, Story } from './types';

const data=<T>(response:{data:T})=>response.data;
export const useMe=()=>useQuery({queryKey:['me'],queryFn:()=>api.get<Profile>('/api/v1/users/me').then(data)});
export const useFeed=()=>useQuery({queryKey:['feed'],queryFn:()=>api.get<FeedPage>('/api/v1/feed').then(data)});
export const useStories=()=>useQuery({queryKey:['stories'],queryFn:()=>api.get<Story[]>('/api/v1/stories').then(data)});
export const useClubs=(mine=false,query='')=>useQuery({queryKey:['clubs',mine,query],queryFn:()=>api.get<Club[]>('/api/v1/clubs',{params:{mine,query}}).then(data)});
export const useClub=(id?:string)=>useQuery({queryKey:['club',id],queryFn:()=>api.get<Club>(`/api/v1/clubs/${id}`).then(data),enabled:Boolean(id)});
export const useEvents=(clubId?:string,query='')=>useQuery({queryKey:['events',clubId,query],queryFn:()=>api.get<EventItem[]>('/api/v1/events',{params:{clubId,query}}).then(data)});
export const useEvent=(id?:string)=>useQuery({queryKey:['event',id],queryFn:()=>api.get<EventItem>(`/api/v1/events/${id}`).then(data),enabled:Boolean(id)});
export const useNotifications=(filter='ALL')=>useQuery({queryKey:['notifications',filter],queryFn:()=>api.get<NotificationItem[]>('/api/v1/notifications',{params:{filter}}).then(data)});
export const useThreads=()=>useQuery({queryKey:['chat','threads'],queryFn:()=>api.get<ChatThread[]>('/api/v1/chat/threads').then(data)});
export const useMessages=(threadId?:string)=>useQuery({queryKey:['chat','messages',threadId],queryFn:()=>api.get<ChatMessage[]>(`/api/v1/chat/threads/${threadId}/messages`).then(data),enabled:Boolean(threadId)});
export const useComments=(postId?:string)=>useQuery({queryKey:['comments',postId],queryFn:()=>api.get<Comment[]>(`/api/v1/posts/${postId}/comments`).then(data),enabled:Boolean(postId)});
export const useSearch=(query:string)=>useQuery({queryKey:['search',query],queryFn:()=>api.get<SearchResult>('/api/v1/search',{params:{query}}).then(data),enabled:query.trim().length>0});
export const useUserProfile=(id?:string)=>useQuery({queryKey:['profile',id],queryFn:()=>api.get<Profile>(id?`/api/v1/users/${id}`:'/api/v1/users/me').then(data)});
export const useFollowers=(id?:string,enabled=true)=>useQuery({queryKey:['profile',id,'followers'],queryFn:()=>api.get<ProfileListItem[]>(`/api/v1/users/${id}/followers`).then(data),enabled:Boolean(id)&&enabled});
export const useFollowing=(id?:string,enabled=true)=>useQuery({queryKey:['profile',id,'following'],queryFn:()=>api.get<ProfileListItem[]>(`/api/v1/users/${id}/following`).then(data),enabled:Boolean(id)&&enabled});
export const useMemberClubs=(id?:string,enabled=true)=>useQuery({queryKey:['profile',id,'clubs'],queryFn:()=>api.get<MemberClub[]>(`/api/v1/users/${id}/clubs`).then(data),enabled:Boolean(id)&&enabled});
export const useUserPosts=(id?:string)=>useQuery({queryKey:['userPosts',id],queryFn:()=>api.get<Array<{id:string;content:string;createdAt:string;likeCount:number;commentCount:number}>>(`/api/v1/users/${id}/posts`).then(data),enabled:Boolean(id)});

/** Finds an existing direct conversation or creates it, then refreshes its list. */
export function useDirectThread(){const qc=useQueryClient();return useMutation({mutationFn:(participantId:string)=>api.post<{id:string}>('/api/v1/chat/threads/direct',{participantId}).then(data),onSuccess:()=>qc.invalidateQueries({queryKey:['chat','threads']})});}
export function useProfileActions(profileId?:string){const qc=useQueryClient();const refresh=()=>Promise.all([qc.invalidateQueries({queryKey:['me']}),qc.invalidateQueries({queryKey:['profile']})]);
 const update=useMutation({mutationFn:(body:Record<string,string>)=>api.patch('/api/v1/users/me',body),onSuccess:refresh});
 const follow=useMutation({mutationFn:(following:boolean)=>following?api.delete(`/api/v1/users/${profileId}/follow`):api.put(`/api/v1/users/${profileId}/follow`),onSuccess:refresh});
 return{update,follow};}

export function useCreateClub(){return useMutation({mutationFn:async(form:FormData)=>{const upload=async(name:string)=>{const file=form.get(name);if(!(file instanceof File)||!file.size)return null;const media=new FormData();media.append('file',file);media.append('category','CLUB');return api.post<{originalPath:string}>('/api/v1/media',media).then(data).then(result=>result.originalPath);};const[logoPath,coverPath]=await Promise.all([upload('logo'),upload('cover')]);return api.post<{id:string}>('/api/v1/clubs',{name:String(form.get('name')),category:String(form.get('category')),description:String(form.get('description')),logoPath,coverPath}).then(data);}});}
export function useCreateEvent(){const qc=useQueryClient();return useMutation({mutationFn:async(form:FormData)=>{const clubId=String(form.get('clubId'));const endsAt=String(form.get('endsAt'));const poster=form.get('poster');let posterPath:string|null=null;if(poster instanceof File&&poster.size){const media=new FormData();media.append('file',poster);media.append('category','CLUB');posterPath=await api.post<{originalPath:string}>('/api/v1/media',media).then(data).then(result=>result.originalPath);}return api.post<{id:string}>(`/api/v1/clubs/${clubId}/events`,{title:String(form.get('title')),description:String(form.get('description')),posterPath,startsAt:new Date(String(form.get('startsAt'))).toISOString(),endsAt:endsAt?new Date(endsAt).toISOString():null,location:String(form.get('location')),category:String(form.get('category'))}).then(data);},onSuccess:()=>qc.invalidateQueries({queryKey:['events']})});}

export function useActions(){const qc=useQueryClient();const invalidate=(keys:string[])=>Promise.all(keys.map(key=>qc.invalidateQueries({queryKey:[key]})));
 const like=useMutation({mutationFn:({id,liked}:{id:string;liked:boolean})=>liked?api.delete(`/api/v1/posts/${id}/like`):api.put(`/api/v1/posts/${id}/like`),onSuccess:()=>invalidate(['feed'])});
 const joinClub=useMutation({mutationFn:(id:string)=>api.put(`/api/v1/clubs/${id}/join`),onSuccess:()=>invalidate(['clubs','club','me'])});
 const joinEvent=useMutation({mutationFn:(id:string)=>api.put(`/api/v1/events/${id}/join`),onSuccess:()=>invalidate(['events','event','clubs'])});
 const markNotification=useMutation({mutationFn:(id:string)=>api.put(`/api/v1/notifications/${id}/read`),onSuccess:()=>invalidate(['notifications'])});
 const markAllNotifications=useMutation({mutationFn:()=>api.put('/api/v1/notifications/read-all'),onSuccess:()=>invalidate(['notifications'])});
 const uploadFile=async(file:File,category:'AVATAR'|'POST'|'STORY'|'CLUB'):Promise<MediaPaths>=>{const form=new FormData();form.append('file',file);form.append('category',category);return api.post<MediaPaths>('/api/v1/media',form).then(data);}; const upload=Object.assign(uploadFile,{mutateAsync:uploadFile,isPending:false});
 return{like,joinClub,joinEvent,markNotification,markAllNotifications,upload};}
