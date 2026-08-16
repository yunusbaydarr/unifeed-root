import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import { en } from './locales/en';
import { tr } from './locales/tr';

export type SupportedLanguage = 'tr' | 'en';
export const supportedLanguages: SupportedLanguage[] = ['tr', 'en'];

const storedLanguage = (): SupportedLanguage => {
  try {
    const stored = window.localStorage.getItem('unifeed.language');
    if (stored === 'tr' || stored === 'en') return stored;
  } catch { /* Storage can be unavailable in hardened browser contexts. */ }
  return navigator.language.toLowerCase().startsWith('en') ? 'en' : 'tr';
};

const initialLanguage = storedLanguage();
if (typeof document !== 'undefined') document.documentElement.lang = initialLanguage;

const authCopy = {
  tr: { common: { or: 'veya', testVersion: 'Test sürümü' }, auth: { campusAccess: 'Kampüs erişimi', welcomeBack: 'Tekrar hoş geldin', signInHint: 'Kampüs topluluğuna devam etmek için giriş yap.', testWarning: 'Yalnızca akademik e-posta adresleri kabul edilir.', togglePassword: 'Şifre görünürlüğünü değiştir', forgot: 'Şifremi unuttum', signIn: 'Giriş yap', continueGoogle: 'Google ile devam et', signUp: 'Kayıt ol', joinCampus: 'Kampüse katıl', createAccount: 'Hesap oluştur', createHint: 'Akademik e-posta adresinle topluluğa katıl.', domainHint: '.edu ve .edu.tr uzantılı adresler desteklenir.', fullName: 'Ad soyad', haveAccount: 'Zaten hesabın var mı?', verifyHint: 'E-postana gönderilen altı haneli kodu gir.', otp: 'Doğrulama kodu', resetHint: 'Sıfırlama bağlantısı için e-posta adresini gir.', resetSent: 'Sıfırlama kodu gönderildi.', enterResetCode: 'Kodu gir', sendCode: 'Kod gönder', savePassword: 'Şifreyi kaydet', finishing: 'Giriş tamamlanıyor', pleaseWait: 'Lütfen bekle…', backLogin: 'Girişe dön' } },
  en: { common: { or: 'or', testVersion: 'Test version' }, auth: { campusAccess: 'Campus access', welcomeBack: 'Welcome back', signInHint: 'Sign in to continue to your campus community.', testWarning: 'Only academic email addresses are accepted.', togglePassword: 'Toggle password visibility', forgot: 'Forgot password?', signIn: 'Sign in', continueGoogle: 'Continue with Google', signUp: 'Sign up', joinCampus: 'Join campus', createAccount: 'Create account', createHint: 'Join with your academic email address.', domainHint: '.edu and .edu.tr addresses are supported.', fullName: 'Full name', haveAccount: 'Already have an account?', verifyHint: 'Enter the six-digit code sent to your email.', otp: 'Verification code', resetHint: 'Enter your email to receive a reset code.', resetSent: 'Reset code sent.', enterResetCode: 'Enter the code', sendCode: 'Send code', savePassword: 'Save password', finishing: 'Finishing sign-in', pleaseWait: 'Please wait…', backLogin: 'Back to sign in' } },
};

void i18n.use(initReactI18next).init({
  resources: {
    tr: { translation: { ...tr.translation, common: { ...tr.translation.common, ...authCopy.tr.common }, auth: { ...tr.translation.auth, ...authCopy.tr.auth } } },
    en: { translation: { ...en.translation, common: { ...en.translation.common, ...authCopy.en.common }, auth: { ...en.translation.auth, ...authCopy.en.auth } } },
  },
  lng: initialLanguage,
  fallbackLng: 'tr',
  supportedLngs: supportedLanguages,
  interpolation: { escapeValue: false },
  returnNull: false,
});

export const changeLanguage = async (language: SupportedLanguage): Promise<void> => {
  await i18n.changeLanguage(language);
  document.documentElement.lang = language;
  try { window.localStorage.setItem('unifeed.language', language); }
  catch { /* Language remains active for the current session. */ }
};

export default i18n;
export { i18n };
