import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Avatar, Dialog, EmptyState, Skeleton } from '@/components/ui';
import { mediaUrl, useFollowers, useFollowing, useMemberClubs } from '@/features/data';

export type ProfileListKind = 'followers' | 'following' | 'clubs';

type Props = {
  userId: string;
  list: ProfileListKind | null;
  onOpenChange: (open: boolean) => void;
};

/** Lazy-loads only the selected profile relation, keeping profile page concerns focused. */
export function ProfileListsDialog({ userId, list, onOpenChange }: Props) {
  const { t } = useTranslation();
  const followers = useFollowers(userId, list === 'followers');
  const following = useFollowing(userId, list === 'following');
  const clubs = useMemberClubs(userId, list === 'clubs');
  const users = list === 'followers' ? followers : list === 'following' ? following : undefined;
  const title = list === 'followers' ? t('profile.followersTitle') : list === 'following' ? t('profile.followingTitle') : t('profile.clubsTitle');

  return <Dialog open={Boolean(list)} onOpenChange={onOpenChange} title={title}>
    <div className="grid max-h-80 gap-2 overflow-y-auto">
      {list === 'clubs' ? (clubs.isLoading ? <Skeleton className="h-24" /> : clubs.data?.length ? clubs.data.map(club => <Link key={club.id} to={`/clubs/${club.id}`} className="flex items-center gap-3 rounded-xl p-3 hover:bg-muted"><Avatar src={mediaUrl(club.logoPath)} name={club.name} /><span><b className="block">{club.name}</b><small className="text-muted-foreground">{club.memberCount} {t('clubs.memberLabel')}</small></span></Link>) : <EmptyState title={t('profile.listEmpty')} />) : (users?.isLoading ? <Skeleton className="h-24" /> : users?.data?.length ? users.data.map(user => <Link key={user.id} to={`/users/${user.id}`} className="flex items-center gap-3 rounded-xl p-3 hover:bg-muted"><Avatar src={mediaUrl(user.avatarPath)} name={user.displayName} /><span><b className="block">{user.displayName}</b><small className="text-muted-foreground">{user.department || t('common.student')}</small></span></Link>) : <EmptyState title={t('profile.listEmpty')} />)}
    </div>
  </Dialog>;
}
