create table calculation
(
    id               serial primary key,
    a                int,
    b                int,
    calculation_type varchar(50),
    result           int
);
