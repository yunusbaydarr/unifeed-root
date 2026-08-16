import { expect, test, type Page, type Request } from '@playwright/test';

const me = {
  id: 'user-1', email: 'student@unifeed.edu', displayName: 'Deniz Yılmaz', avatarPath: null,
  bio: 'Bilgisayar mühendisliği öğrencisi', department: 'Bilgisayar Mühendisliği', academicYear: '3',
  globalRole: 'USER', testAccount: false, followerCount: 12, followingCount: 8, clubCount: 2, following: false,
};

async function mockApi(page: Page, authenticated = true) {
  const requests: Request[] = [];
  await page.addInitScript(() => window.localStorage.setItem('unifeed.language', 'tr'));
  await page.route('**/api/v1/**', async route => {
    const request = route.request();
    requests.push(request);
    const url = new URL(request.url());
    const path = url.pathname;
    const method = request.method();
    const json = (body: unknown, status = 200) => route.fulfill({ status, contentType: 'application/json', body: JSON.stringify(body) });

    if (path.includes('/api/v1/api/v1/')) return json({ detail: 'Duplicated API prefix' }, 404);
    if (path === '/api/v1/auth/refresh' && method === 'POST') return authenticated ? json({ accessToken: 'test-token', testAccount: false }) : json({ detail: 'Anonymous' }, 401);
    if (path === '/api/v1/auth/login' && method === 'POST') return json({ accessToken: 'test-token', testAccount: false });
    if (path === '/api/v1/users/me' && method === 'GET') return json(me);
    if (path === '/api/v1/feed' && method === 'GET') return json({ items: [{ id: 'post-1', authorId: 'user-2', authorName: 'Ada Kaya', authorAvatarPath: null, content: 'Robotik atölyesi bugün 18.00’de!', likeCount: 4, commentCount: 1, createdAt: '2026-08-14T12:00:00Z', liked: false, mediaPaths: [] }], nextCursorCreatedAt: null, nextCursorId: null });
    if (path === '/api/v1/stories' && method === 'GET') return json([{ id: 'story-1', authorId: 'user-2', authorName: 'Ada Kaya', authorAvatarPath: null, mediaPath: '/uploads/story.jpg', expiresAt: '2026-08-15T12:00:00Z', viewCount: 1, createdAt: '2026-08-14T12:00:00Z' }]);
    if (path === '/api/v1/posts' && method === 'POST') return json({ id: 'post-new' });
    if (path === '/api/v1/stories/story-1/view' && method === 'PUT') return route.fulfill({ status: 204 });
    if (path === '/api/v1/notifications' && method === 'GET') return json([]);
    if (path === '/api/v1/chat/threads' && method === 'GET') return json([{ id: 'thread-1', type: 'DIRECT', clubId: null, title: 'Ada Kaya', lastMessage: 'Merhaba', lastMessageAt: '2026-08-14T12:00:00Z', unreadCount: 1 }]);
    if (path === '/api/v1/chat/threads/thread-1/messages' && method === 'GET') return json([]);
    if (path === '/api/v1/chat/threads/thread-1/read' && method === 'PUT') return route.fulfill({ status: 204 });
    if (path === '/api/v1/users/user-1/posts' && method === 'GET') return json([]);
    if (path === '/api/v1/search' && method === 'GET') return json({ users: [], clubs: [], events: [] });
    return json({ detail: `Unhandled ${method} ${path}` }, 404);
  });
  return requests;
}

test('authenticated home renders feed and composer posts to the real contract path', async ({ page }) => {
  const requests = await mockApi(page);
  await page.goto('/');
  await expect(page.getByRole('heading', { name: 'Bugün neler oluyor?' })).toBeVisible();
  await expect(page.getByText('Robotik atölyesi bugün 18.00’de!')).toBeVisible();
  await page.getByRole('button', { name: 'Gönderi oluştur' }).click();
  await page.getByRole('textbox', { name: /Ne düşünüyorsun/ }).fill('Yeni dönem buluşması');
  await page.getByRole('button', { name: 'Yayınla' }).click();
  await expect(page.getByText('Gönderi yayınlandı.')).toBeVisible();
  expect(requests.some(r => new URL(r.url()).pathname === '/api/v1/posts' && r.method() === 'POST')).toBeTruthy();
  expect(requests.some(r => new URL(r.url()).pathname.includes('/api/v1/api/v1/'))).toBeFalsy();
});

test('anonymous user can log in and is redirected to the feed', async ({ page }) => {
  await mockApi(page, false);
  await page.goto('/auth/login');
  await page.getByRole('textbox', { name: 'E-posta' }).fill('student@unifeed.edu');
  await page.locator('input[name="password"]').fill('secret123');
  await page.getByRole('button', { name: 'Giriş yap' }).click();
  await expect(page).toHaveURL(/\/$/);
  await expect(page.getByRole('heading', { name: 'Bugün neler oluyor?' })).toBeVisible();
});

test('mobile shell exposes key destinations and settings', async ({ page }, testInfo) => {
  test.skip(!testInfo.project.name.includes('mobile'), 'Mobile navigation assertion');
  await mockApi(page);
  await page.goto('/');
  const nav = page.getByRole('navigation', { name: 'Mobil navigasyon' });
  await expect(nav.getByText('Ana Sayfa')).toBeVisible();
  await expect(nav.getByText('Mesajlar')).toBeVisible();
  await expect(nav.getByText('Profil')).toBeVisible();
  await expect(page.getByRole('button', { name: 'Ayarlar' })).toBeVisible();
  await nav.getByText('Mesajlar').click();
  await expect(page).toHaveURL(/\/messages/);
});

test('mobile message thread can be opened and closed back to the list', async ({ page }, testInfo) => {
  test.skip(!testInfo.project.name.includes('mobile'), 'Mobile conversation assertion');
  await mockApi(page);
  await page.goto('/messages');
  await page.getByRole('button', { name: /Ada Kaya/ }).click();
  await expect(page.getByRole('button', { name: 'Geri' })).toBeVisible();
  await page.getByRole('button', { name: 'Geri' }).click();
  await expect(page.getByRole('heading', { name: 'Mesajlar' })).toBeVisible();
  await expect(page.getByRole('button', { name: /Ada Kaya/ })).toBeVisible();
});

test('desktop shell exposes all primary navigation links', async ({ page }, testInfo) => {
  test.skip(testInfo.project.name.includes('mobile'), 'Desktop navigation assertion');
  await mockApi(page);
  await page.goto('/');
  const nav = page.getByRole('navigation', { name: 'Ana menü' });
  for (const label of ['Ana Sayfa', 'Keşfet', 'Kulüp ve Etkinlikler', 'Mesajlar', 'Bildirimler', 'Profil']) {
    await expect(nav.getByText(label, { exact: true })).toBeVisible();
  }
});
