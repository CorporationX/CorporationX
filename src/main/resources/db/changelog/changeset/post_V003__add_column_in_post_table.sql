alter table post
add is_verify varchar(64) not null default 'UNCHECKED',
add verified_date timestamptz