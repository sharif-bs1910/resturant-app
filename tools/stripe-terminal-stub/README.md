# Stripe Terminal stub (dev)

Tiny local server that creates **connection tokens** and **PaymentIntents** (`card_present`) using your Stripe **test** secret key. The Android `dev` flavor points at `http://10.0.2.2:4242/` (emulator → host).

## Setup

1. Rotate any secret that was shared in chat, then create a new test secret in the Stripe Dashboard.
2. Export the key (do not commit it):

```bash
export STRIPE_SECRET_KEY=sk_test_your_rotated_key
```

Or copy `.env.example` to `.env` and load it yourself — `*.env` is gitignored.

3. Start:

```bash
cd tools/stripe-terminal-stub
npm start
```

## Smoke checks

```bash
curl -s -X POST http://127.0.0.1:4242/connection_token
curl -s -X POST http://127.0.0.1:4242/payment_intents \
  -H 'Content-Type: application/json' \
  -d '{"amountCents":100,"currency":"usd"}'
```

## Physical device

Use your machine's LAN IP instead of `10.0.2.2` in `config/env/dev.properties` (`TERMINAL_STUB_BASE_URL`), and ensure the tablet can reach that host.
