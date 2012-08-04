create table UserConnection (userId varchar(100) not null,
    providerId varchar(100) not null,
    providerUserId varchar(100),
    rank int not null,
    displayName varchar(255),
    profileUrl varchar(512),
    imageUrl varchar(512),
    accessToken varchar(1000) not null,					
    secret varchar(1000),
    refreshToken varchar(255),
    expireTime bigint,
    primary key (userId, providerId, providerUserId));
create unique index UserConnectionRank on UserConnection(userId, providerId, rank);