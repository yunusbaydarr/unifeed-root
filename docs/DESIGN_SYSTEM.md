---
name: Vibrant Academic Social
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#464555'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#777587'
  outline-variant: '#c7c4d8'
  surface-tint: '#4d44e3'
  primary: '#3525cd'
  on-primary: '#ffffff'
  primary-container: '#4f46e5'
  on-primary-container: '#dad7ff'
  inverse-primary: '#c3c0ff'
  secondary: '#006c49'
  on-secondary: '#ffffff'
  secondary-container: '#6cf8bb'
  on-secondary-container: '#00714d'
  tertiary: '#8b1b34'
  on-tertiary: '#ffffff'
  tertiary-container: '#ab354b'
  on-tertiary-container: '#ffd0d3'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e2dfff'
  primary-fixed-dim: '#c3c0ff'
  on-primary-fixed: '#0f0069'
  on-primary-fixed-variant: '#3323cc'
  secondary-fixed: '#6ffbbe'
  secondary-fixed-dim: '#4edea3'
  on-secondary-fixed: '#002113'
  on-secondary-fixed-variant: '#005236'
  tertiary-fixed: '#ffdadc'
  tertiary-fixed-dim: '#ffb2b9'
  on-tertiary-fixed: '#400010'
  on-tertiary-fixed-variant: '#891933'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 40px
    fontWeight: '800'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  baseline: 4px
  container-margin: 1rem
  gutter-md: 1rem
  stack-sm: 0.5rem
  stack-md: 1rem
  stack-lg: 1.5rem
---

## Brand & Style

The design system is engineered for a high-energy, student-centric social environment. It balances the rigor of academic life with the fluidity of social interaction. The aesthetic sits at the intersection of **Corporate Modern** and **Glassmorphism**, utilizing structured layouts for information density while employing soft, translucent overlays to maintain a sense of lightness and depth. 

The emotional response should be one of "structured excitement"—feeling organized enough for university administration yet vibrant enough for campus life. Surfaces are clean with generous whitespace, punctuated by high-saturation accents that guide the eye toward interactive elements.

## Colors

The palette is anchored by **University Indigo** (#4F46E5), a deep, trustworthy blue that represents the institutional side of the experience. This is contrasted by **Mint Spark** (#10B981) for growth and success states, and **Coral Pulse** (#FB7185) for social notifications and high-energy calls to action.

In **Light Mode**, surfaces use a tiered grayscale (Slate 50 to 900) to ensure readability.
In **Dark Mode**, the Indigo shifts slightly toward a more luminous violet-blue to maintain contrast against deep charcoal backgrounds (#0F172A). Translucency is used more aggressively in dark mode to prevent the UI from feeling heavy or "muddy."

## Typography

This design system utilizes a dual-font strategy. **Plus Jakarta Sans** provides a friendly, geometric character for headlines, making the app feel approachable and modern. **Inter** is used for all body copy and UI labels to ensure maximum legibility during long reading sessions, such as course updates or forum posts.

Key emphasis is placed on the `display` and `headline` levels to create a clear information hierarchy on mobile screens. Use `label-md` specifically for metadata (e.g., timestamps, category tags) to keep the interface clean without sacrificing context.

## Layout & Spacing

The layout follows a **Fluid Grid** model optimized for narrow viewports. On mobile, the system uses a 4-column grid with 16px (1rem) side margins. On tablet and desktop, this scales to an 8 or 12-column grid respectively, with content capped at a maximum width of 1200px to maintain line-length readability.

Spacing is governed by a 4px baseline. Vertical stack spacing should be used consistently:
- **8px (sm):** Related elements (e.g., avatar and username).
- **16px (md):** Standard component internal padding or spacing between text blocks.
- **24px (lg):** Separation between distinct cards or sections in the feed.

## Elevation & Depth

This design system uses **Tonal Layers** combined with **Ambient Shadows** to create a sense of height. 

- **Level 0 (Base):** The main background color of the app.
- **Level 1 (Cards):** Subtle white/dark-gray surfaces with a very soft, diffused shadow (0px 4px 20px, 5% opacity primary color tint).
- **Level 2 (Overlays/Modals):** Glassmorphic surfaces with a 12px backdrop blur and a thin 1px border (10% white or 10% indigo) to define edges.

Shadows should never be pure black; they are always tinted with the Primary Indigo to keep the "vibrant" brand promise even in the depth effects.

## Shapes

The shape language is consistently **Rounded**, reflecting the "soft and welcoming" aspect of the brand. 
- **Standard (8px):** Used for input fields, post cards, and event banners.
- **Large (16px):** Used for large feature cards and modal containers.
- **Full (Pill):** Reserved for primary buttons and search bars to make them feel highly interactive.

Interactive elements (buttons/inputs) use a 1.5px border weight to ensure they feel substantial on high-density mobile displays.

## Components

### Story Circles
Stories are encased in a 2px gradient ring (Indigo to Mint). Active stories use the gradient, while viewed stories transition to a 1px solid light-gray stroke.

### Post Cards
Cards utilize the Level 1 Elevation. The header includes the user's avatar (rounded) and a 3-dot menu. Media is displayed full-bleed within the card width with a top-only 8px corner radius to stay flush with the card header.

### Primary Buttons
Pill-shaped with a solid Indigo fill. Text is white, bolded Inter. On-press state involves a subtle 2% scale-down to provide tactile feedback.

### Event Banners
Used for campus happenings. These feature a high-contrast Coral Pulse accent bar on the left edge and use Plus Jakarta Sans for the event title to differentiate them from standard social posts.

### Chips & Tags
Small, pill-shaped elements with a 10% opacity fill of the category color (e.g., #10B981 for "Academics") and 100% opacity text of the same color. This creates a "soft tag" look that doesn't compete with primary buttons.

### Input Fields
Outlined style with a 1.5px gray-200 border. Upon focus, the border transitions to 2px Indigo with a soft 4px indigo outer glow.