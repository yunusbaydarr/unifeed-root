import { useState } from 'react';
import { Check, UserPlus, X } from 'lucide-react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { Avatar, Button, Dialog, EmptyState, Skeleton, useToast } from '@/components/ui';
import { api } from '@/lib/api';
import { mediaUrl } from './types';

type InviteCandidate = { id: string; displayName: string; avatarPath?: string | null };

export function InvitationPicker({ resource, resourceId }: { resource: 'clubs' | 'events'; resourceId: string }) {
  const { t } = useTranslation(); const toast = useToast(); const [open, setOpen] = useState(false); const [invitedIds, setInvitedIds] = useState<Set<string>>(new Set());
  const candidates = useQuery({ queryKey: ['invite-candidates', resource, resourceId], queryFn: () => api.get<InviteCandidate[]>(`/api/v1/${resource}/${resourceId}/invite-candidates`).then(response => response.data), enabled: open });
  const invite = useMutation({ mutationFn: (userId: string) => api.post(`/api/v1/${resource}/${resourceId}/invitations`, { userId }), onSuccess: (_, userId) => { setInvitedIds(previous => new Set(previous).add(userId)); toast.success(t('invitations.sent')); }, onError: () => toast.error(t('invitations.sendError')) });
  return <><Button variant="outline" leftIcon={<UserPlus className="size-4" />} onClick={() => setOpen(true)}>{t('clubs.invite')}</Button><Dialog open={open} onOpenChange={setOpen} title={t('invitations.pickerTitle')}>{candidates.isLoading ? <Skeleton className="h-32" /> : candidates.data?.length ? <div className="grid gap-2">{candidates.data.map(candidate => { const invited = invitedIds.has(candidate.id); return <div key={candidate.id} className="flex items-center gap-3 rounded-xl p-2 hover:bg-muted"><Avatar src={mediaUrl(candidate.avatarPath)} name={candidate.displayName} /><b className="min-w-0 flex-1 truncate">{candidate.displayName}</b><Button size="sm" loading={invite.isPending && invite.variables === candidate.id} disabled={invited || invite.isPending} onClick={() => invite.mutate(candidate.id)}>{invited ? t('clubs.invited') : t('clubs.invite')}</Button></div>; })}</div> : <EmptyState title={t('invitations.noCandidates')} />}</Dialog></>;
}

export function InvitationResponseActions({ invitationId, onResponded }: { invitationId: string; onResponded: () => void }) {
  const { t } = useTranslation(); const toast = useToast(); const queryClient = useQueryClient(); const [responded, setResponded] = useState<boolean | null>(null);
  const response = useMutation({ mutationFn: (accept: boolean) => api.put(`/api/v1/notifications/invitations/${invitationId}`, undefined, { params: { accept } }), onSuccess: async (_, accept) => { setResponded(accept); await Promise.all([queryClient.invalidateQueries({ queryKey: ['notifications'] }), accept ? queryClient.invalidateQueries({ queryKey: ['clubs'] }) : Promise.resolve(), accept ? queryClient.invalidateQueries({ queryKey: ['events'] }) : Promise.resolve()]); onResponded(); toast.success(t(accept ? 'invitations.accepted' : 'invitations.declined')); }, onError: () => toast.error(t('invitations.responseError')) });
  const disabled = response.isPending || responded !== null;
  return <div className="mt-3 flex flex-wrap gap-2" onClick={event => event.stopPropagation()}><Button size="sm" loading={response.isPending && response.variables === true} disabled={disabled} leftIcon={<Check className="size-4" />} onClick={() => response.mutate(true)}>{t('invitations.accept')}</Button><Button size="sm" variant="outline" loading={response.isPending && response.variables === false} disabled={disabled} leftIcon={<X className="size-4" />} onClick={() => response.mutate(false)}>{t('invitations.decline')}</Button></div>;
}
