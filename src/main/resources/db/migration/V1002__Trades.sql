-- Migrate old tables (mostly personal preference and to make use of hibernate)
ALTER TABLE trade.account RENAME TO old_account;
ALTER TABLE trade.address RENAME TO old_address;

CREATE TABLE IF NOT EXISTS trade.address
(
  id UUID NOT NULL DEFAULT uuid_generate_v4(),
  name TEXT NOT NULL,
  street TEXT NOT NULL,
  city TEXT NOT NULL,
  state trade.state NOT NULL,
  zipcode NUMERIC(9) NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  updated_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  created_by TEXT,
  updated_by TEXT,
  CONSTRAINT address_pk PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS trade.account
(
  id UUID NOT NULL DEFAULT uuid_generate_v4(),
  address_id UUID,
  username TEXT NOT NULL,
  email TEXT NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  updated_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  created_by TEXT,
  updated_by TEXT,
  CONSTRAINT account_pk PRIMARY KEY(id),
  CONSTRAINT account_username_unique UNIQUE(username),
  CONSTRAINT account_address_fk FOREIGN KEY (address_id) REFERENCES trade.address (id)
);

INSERT INTO trade.address (id, name, street, city, state, created_date, updated_date, created_by, updated_by)
SELECT address_uuid, name, street, city, state, created_date, updated_date, created_by, updated_by
FROM trade.old_address;

INSERT INTO trade.account (id, address_id, username, email, created_date, updated_date, created_by, updated_by)
SELECT account_uuid, address_uuid, username, email, created_date, updated_date, created_by, updated_by
FROM trade.old_account;

DROP TABLE trade.old_account;
DROP TABLE trade.old_address;

-- Trade table
CREATE TYPE trade.symbol as ENUM (
    'AAPL',
    'MSFT'
);
CREATE TYPE trade.side as ENUM (
    'BUY',
    'SELL'
);
CREATE TYPE trade.status as ENUM (
    'SUBMITTED',
    'CANCELLED',
    'COMPLETED',
    'FAILED'
);

CREATE TABLE IF NOT EXISTS trade.trade
(
  id UUID NOT NULL DEFAULT uuid_generate_v4(),
  account_id UUID NOT NULL DEFAULT uuid_generate_v4(),
  quantity NUMERIC NOT NULL,
  price MONEY NOT NULL,
  side trade.side NOT NULL,
  symbol trade.symbol NOT NULL,
  status trade.status NOT NULL,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  updated_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  created_by TEXT,
  updated_by TEXT,
  CONSTRAINT trade_pk PRIMARY KEY(id),
  CONSTRAINT trade_account_fk FOREIGN KEY (account_id) REFERENCES trade.account (id)
);