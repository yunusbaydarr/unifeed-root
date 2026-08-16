import { lazy, Suspense } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { MainLayout } from '@/components/app';
import { ProtectedRoute } from '@/features/auth';
import { Spinner } from '@/components/ui';

const LoginPage=lazy(()=>import('@/pages/auth/AuthPages').then(m=>({default:m.LoginPage})));
const RegisterPage=lazy(()=>import('@/pages/auth/AuthPages').then(m=>({default:m.RegisterPage})));
const VerifyOtpPage=lazy(()=>import('@/pages/auth/AuthPages').then(m=>({default:m.VerifyOtpPage})));
const ForgotPasswordPage=lazy(()=>import('@/pages/auth/AuthPages').then(m=>({default:m.ForgotPasswordPage})));
const ResetPasswordPage=lazy(()=>import('@/pages/auth/AuthPages').then(m=>({default:m.ResetPasswordPage})));
const HomePage=lazy(()=>import('@/pages/FeedPages').then(m=>({default:m.HomePage})));
const PostDetailPage=lazy(()=>import('@/pages/FeedPages').then(m=>({default:m.PostDetailPage})));
const ExplorePage=lazy(()=>import('@/pages/FeedPages').then(m=>({default:m.ExplorePage})));
const CommunityPage=lazy(()=>import('@/pages/CommunityPages').then(m=>({default:m.CommunityPage})));
const ClubCreatePage=lazy(()=>import('@/pages/CommunityPages').then(m=>({default:m.ClubCreatePage})));
const ClubDetailPage=lazy(()=>import('@/pages/CommunityPages').then(m=>({default:m.ClubDetailPage})));
const EventCreatePage=lazy(()=>import('@/pages/CommunityPages').then(m=>({default:m.EventCreatePage})));
const EventDetailPage=lazy(()=>import('@/pages/CommunityPages').then(m=>({default:m.EventDetailPage})));
const NotificationsPage=lazy(()=>import('@/pages/AccountPages').then(m=>({default:m.NotificationsPage})));
const MessagesPage=lazy(()=>import('@/pages/AccountPages').then(m=>({default:m.MessagesPage})));
const ProfilePage=lazy(()=>import('@/pages/AccountPages').then(m=>({default:m.ProfilePage})));
const SettingsPage=lazy(()=>import('@/pages/AccountPages').then(m=>({default:m.SettingsPage})));
const NotFoundPage=lazy(()=>import('@/pages/AccountPages').then(m=>({default:m.NotFoundPage})));

const loading=<main className="grid min-h-dvh place-items-center bg-background"><Spinner size="lg" aria-label="Oturum yükleniyor"/></main>;

export default function App(){return <Suspense fallback={loading}><Routes>
  <Route path="/auth/login" element={<LoginPage/>}/><Route path="/login" element={<Navigate to="/auth/login" replace/>}/>
  <Route path="/auth/register" element={<RegisterPage/>}/><Route path="/auth/verify" element={<VerifyOtpPage/>}/>
  <Route path="/auth/forgot-password" element={<ForgotPasswordPage/>}/><Route path="/auth/reset-password" element={<ResetPasswordPage/>}/>
  <Route element={<ProtectedRoute redirectTo="/auth/login" fallback={loading}/>}> <Route element={<MainLayout/>}>
    <Route index element={<HomePage/>}/><Route path="posts/:id" element={<PostDetailPage/>}/><Route path="explore" element={<ExplorePage/>}/><Route path="community" element={<CommunityPage/>}/>
    <Route path="clubs/new" element={<ClubCreatePage/>}/><Route path="clubs/:id" element={<ClubDetailPage/>}/>
    <Route path="events/new" element={<EventCreatePage/>}/><Route path="events/:id" element={<EventDetailPage/>}/>
    <Route path="notifications" element={<NotificationsPage/>}/><Route path="messages" element={<MessagesPage/>}/>
    <Route path="profile" element={<ProfilePage/>}/><Route path="users/:id" element={<ProfilePage/>}/><Route path="settings" element={<SettingsPage/>}/>
  </Route></Route><Route path="*" element={<NotFoundPage/>}/>
</Routes></Suspense>}
