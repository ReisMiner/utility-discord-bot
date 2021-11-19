create table servers
(
    server_id    varchar(20) primary key not null,
    server_name  text,
    date_joined  datetime,
    coin_formula text default '1',
    bank_balance bigint(19) default '0'
);

create table shop_items
(
    item_id     int auto_increment primary key,
    server_id   varchar(20) not null,
    role        text,
    description text,
    price bigint(19),
    foreign key (server_id) references servers (server_id)
);

create table user_coins
(
    user_id     varchar(20) not null,
    server_id   varchar(20) not null,
    coin_amount bigint(19) default 0,
    CONSTRAINT user_coins_id PRIMARY KEY (user_id, server_id),
    FOREIGN KEY (server_id) references servers(server_id)
);