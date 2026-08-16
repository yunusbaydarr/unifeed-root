import { useState } from 'react';
import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { Button } from './Button';
import { Dialog } from './Dialog';
import { Input } from './Input';

describe('atomic form controls', () => {
  it('uses a safe button type and blocks interaction while loading', async () => {
    const onClick = vi.fn();
    const { rerender } = render(<Button onClick={onClick}>Kaydet</Button>);
    expect(screen.getByRole('button', { name: 'Kaydet' })).toHaveAttribute('type', 'button');
    await userEvent.click(screen.getByRole('button', { name: 'Kaydet' }));
    expect(onClick).toHaveBeenCalledOnce();

    rerender(<Button loading onClick={onClick}>Kaydet</Button>);
    expect(screen.getByRole('button')).toBeDisabled();
    expect(screen.getByRole('button')).toHaveAttribute('aria-busy', 'true');
  });

  it('associates input labels, hints and validation state accessibly', () => {
    render(<Input label="E-posta" hint="Üniversite adresin" error="Geçersiz adres" required />);
    const input = screen.getByRole('textbox', { name: /E-posta/ });
    expect(input).toBeRequired();
    expect(input).toHaveAttribute('aria-invalid', 'true');
    expect(screen.getByText('Geçersiz adres')).toHaveAttribute('id', input.getAttribute('aria-describedby'));
  });
});

function DialogHarness() {
  const [open, setOpen] = useState(false);
  return <><button onClick={() => setOpen(true)}>Aç</button><Dialog open={open} onOpenChange={setOpen} title="Yeni gönderi"><Input label="İçerik" /><Button>Kaydet</Button></Dialog></>;
}

describe('Dialog', () => {
  it('announces itself, traps focus and closes with Escape', async () => {
    const user = userEvent.setup();
    render(<DialogHarness />);
    const trigger = screen.getByRole('button', { name: 'Aç' });
    await user.click(trigger);
    const dialog = screen.getByRole('dialog', { name: 'Yeni gönderi' });
    expect(dialog).toBeInTheDocument();
    expect(within(dialog).getByRole('button', { name: 'Kapat' })).toHaveFocus();
    await user.keyboard('{Escape}');
    expect(dialog).not.toBeInTheDocument();
    expect(trigger).toHaveFocus();
  });
});
