USE master;
GO

-- =========================================================================
-- 1. LÀM SẠCH VÀ TẠO DATABASE
-- =========================================================================
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'BlindBoxDB')
BEGIN
    ALTER DATABASE BlindBoxDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE BlindBoxDB;
END
GO

CREATE DATABASE BlindBoxDB;
GO

USE BlindBoxDB;
GO

-- =========================================================================
-- 2. TẠO CẤU TRÚC BẢNG (SCHEMA)
-- =========================================================================

-- Bảng Users
CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(50) NOT NULL UNIQUE,
    PasswordHash NVARCHAR(255) NOT NULL,
    Email NVARCHAR(100),
    FullName NVARCHAR(100),
    Address NVARCHAR(MAX),
    PhoneNumber NVARCHAR(20),
    WalletBalance DECIMAL(18, 2) DEFAULT 0,
    IsAdmin BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT CK_Wallet_NonNegative CHECK (WalletBalance >= 0)
);

-- Bảng Categories (Danh mục)
CREATE TABLE Categories (
    CategoryID INT IDENTITY(1,1) PRIMARY KEY,
    CategoryName NVARCHAR(100) NOT NULL,
    Description NVARCHAR(MAX),
    ImageURL NVARCHAR(MAX),
    IsActive BIT DEFAULT 1
);

-- Bảng BlindBoxes (Hộp mù)
CREATE TABLE BlindBoxes (
    BoxID INT IDENTITY(1,1) PRIMARY KEY,
    BoxName NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX),
    Price DECIMAL(18, 2) NOT NULL,
    ImageURL NVARCHAR(MAX),
    IsActive BIT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE(),
    CategoryID INT FOREIGN KEY REFERENCES Categories(CategoryID)
);

-- Bảng BoxItems (Vật phẩm trong hộp)
CREATE TABLE BoxItems (
    ItemID INT IDENTITY(1,1) PRIMARY KEY,
    BoxID INT FOREIGN KEY REFERENCES BlindBoxes(BoxID) ON DELETE CASCADE,
    ItemName NVARCHAR(255) NOT NULL,
    ImageURL NVARCHAR(MAX),
    RarityLevel VARCHAR(10) NOT NULL, -- S, A, B, C, D
    Probability FLOAT DEFAULT 0,      -- Tỉ lệ (0.01 = 1%)
    MarketValue DECIMAL(18, 2) DEFAULT 0,
    StockQuantity INT DEFAULT 0,
    IsHidden BIT DEFAULT 1,
    IsPityReward BIT DEFAULT 0
);

-- Bảng UserInventory (Kho đồ)
CREATE TABLE UserInventory (
    InventoryID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    ItemID INT FOREIGN KEY REFERENCES BoxItems(ItemID),
    ObtainedDate DATETIME DEFAULT GETDATE(),
    Status NVARCHAR(50) DEFAULT 'IN_STORAGE', -- IN_STORAGE, SOLD_BACK, REQUESTED_SHIP
    SoldPrice DECIMAL(18, 2) DEFAULT 0
);

-- Bảng Transactions (Giao dịch)
CREATE TABLE Transactions (
    TransactionID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    Amount DECIMAL(18, 2) NOT NULL,
    TransactionType NVARCHAR(50), -- DEPOSIT, BUY_BOX, SELL_BACK
    Description NVARCHAR(255),
    TransactionDate DATETIME DEFAULT GETDATE()
);

-- Bảng UserPityStats (Bảo hiểm)
CREATE TABLE UserPityStats (
    StatID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    BoxID INT FOREIGN KEY REFERENCES BlindBoxes(BoxID),
    SpinsWithoutS INT DEFAULT 0,
    LastSRewardDate DATETIME
);

-- Bảng ShipmentRequests (Yêu cầu giao hàng)
CREATE TABLE ShipmentRequests (
    ShipmentID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    ReceiverName NVARCHAR(100),
    PhoneNumber NVARCHAR(20),
    Address NVARCHAR(MAX),
    Note NVARCHAR(MAX),
    RequestDate DATETIME DEFAULT GETDATE(),
    ShipmentStatus NVARCHAR(50) DEFAULT 'PENDING'
);

CREATE TABLE ShipmentDetails (
    DetailID INT IDENTITY(1,1) PRIMARY KEY,
    ShipmentID INT FOREIGN KEY REFERENCES ShipmentRequests(ShipmentID) ON DELETE CASCADE,
    InventoryID INT FOREIGN KEY REFERENCES UserInventory(InventoryID)
);

-- Bảng Banners
CREATE TABLE Banners (
    BannerID INT IDENTITY(1,1) PRIMARY KEY,
    Title NVARCHAR(100),
    ImageURL NVARCHAR(MAX) NOT NULL,
    LinkUrl NVARCHAR(MAX),
    DisplayOrder INT DEFAULT 0,
    IsActive BIT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- Bảng News
CREATE TABLE News (
    NewsID INT IDENTITY(1,1) PRIMARY KEY,
    Title NVARCHAR(200) NOT NULL,
    Content NVARCHAR(MAX),
    Thumbnail NVARCHAR(MAX),
    PublishedAt DATETIME DEFAULT GETDATE()
);

-- Bảng Carts & Orders
CREATE TABLE Products ( -- Sản phẩm thường (nếu có)
    ProductID INT IDENTITY(1,1) PRIMARY KEY,
    ProductName NVARCHAR(255),
    Price DECIMAL(18, 2),
    CategoryID INT
);

CREATE TABLE Carts (
    CartID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CreatedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE CartItems (
    CartItemID INT IDENTITY(1,1) PRIMARY KEY,
    CartID INT FOREIGN KEY REFERENCES Carts(CartID) ON DELETE CASCADE,
    BoxID INT FOREIGN KEY REFERENCES BlindBoxes(BoxID),
    ProductID INT FOREIGN KEY REFERENCES Products(ProductID),
    Quantity INT DEFAULT 1,
    AddedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE Orders (
    OrderID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    OrderDate DATETIME DEFAULT GETDATE(),
    TotalAmount DECIMAL(18, 2) NOT NULL,
    OrderStatus NVARCHAR(50) DEFAULT 'PENDING'
);

CREATE TABLE OrderDetails (
    OrderDetailID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT FOREIGN KEY REFERENCES Orders(OrderID) ON DELETE CASCADE,
    BoxID INT,
    ProductID INT,
    Quantity INT,
    PriceAtTime DECIMAL(18,2)
);

CREATE TABLE Reviews (
    ReviewID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT,
    BoxID INT,
    Rating INT,
    Comment NVARCHAR(MAX),
    ReviewDate DATETIME
);

CREATE TABLE Feedbacks (
    FeedbackID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT,
    Content NVARCHAR(MAX),
    SentAt DATETIME DEFAULT GETDATE()
);

GO

-- =========================================================================
-- 3. NẠP DỮ LIỆU CƠ BẢN (USERS, CATEGORIES)
-- =========================================================================

-- Users
INSERT INTO Users (Username, PasswordHash, FullName, WalletBalance, IsAdmin, Email)
VALUES 
('admin', '123456', N'Quản Trị Viên', 0, 1, 'admin@blindbox.com'),
('vinh_test', '123456', N'Nguyễn Xuân Vinh', 5000000, 0, 'vinh@test.com');

-- Categories
INSERT INTO Categories (CategoryName, ImageURL) VALUES 
(N'Anime', NULL), 
(N'Game', NULL), 
(N'Plushie', NULL);

-- =========================================================================
-- 4. NẠP DỮ LIỆU CÁC HỘP (BOXES & ITEMS)
-- =========================================================================

-- -------------------------------------------------------------------------
-- BOX 1: GẤU BÔNG PLUSHIE (Dùng ảnh Local)
-- -------------------------------------------------------------------------
INSERT INTO BlindBoxes (BoxName, Price, ImageURL, Description, CategoryID, IsActive)
VALUES (N'Hộp Bí Mật Plushie (Plushie Box)', 100000, '/images/box.jpg', N'Sưu tập trọn bộ 30 gấu bông siêu cấp đáng yêu!', 3, 1);

DECLARE @Box1 INT = SCOPE_IDENTITY();

-- Insert Items (Dùng vòng lặp tạo nhanh)
INSERT INTO BoxItems (BoxID, ItemName, ImageURL, RarityLevel, Probability, MarketValue, StockQuantity, IsPityReward)
VALUES (@Box1, N'Gấu Hoàng Gia (S)', '/images/rank_s.jpg', 'S', 0.01, 2000000, 5, 1);

DECLARE @i INT = 1;
WHILE @i <= 2 BEGIN -- Rank A
    INSERT INTO BoxItems (BoxID, ItemName, ImageURL, RarityLevel, Probability, MarketValue, StockQuantity) VALUES (@Box1, N'A Plushie #' + CAST(@i AS NVARCHAR), '/images/rank_a.jpg', 'A', 0.02, 500000, 20);
    SET @i = @i + 1;
END
SET @i = 1; WHILE @i <= 5 BEGIN -- Rank B
    INSERT INTO BoxItems (BoxID, ItemName, ImageURL, RarityLevel, Probability, MarketValue, StockQuantity) VALUES (@Box1, N'B Plushie #' + CAST(@i AS NVARCHAR), '/images/rank_b.jpg', 'B', 0.03, 150000, 50);
    SET @i = @i + 1;
END
SET @i = 1; WHILE @i <= 7 BEGIN -- Rank C
    INSERT INTO BoxItems (BoxID, ItemName, ImageURL, RarityLevel, Probability, MarketValue, StockQuantity) VALUES (@Box1, N'C Plushie #' + CAST(@i AS NVARCHAR), '/images/rank_c.jpg', 'C', 0.0428, 80000, 100);
    SET @i = @i + 1;
END
SET @i = 1; WHILE @i <= 15 BEGIN -- Rank D
    INSERT INTO BoxItems (BoxID, ItemName, ImageURL, RarityLevel, Probability, MarketValue, StockQuantity) VALUES (@Box1, N'D Plushie #' + CAST(@i AS NVARCHAR), '/images/rank_d.jpg', 'D', 0.0333, 20000, 200);
    SET @i = @i + 1;
END

-- -------------------------------------------------------------------------
-- BOX 2: POKEMON TCG (Box: Local, Item: Online API)
-- -------------------------------------------------------------------------
INSERT INTO BlindBoxes (BoxName, Price, ImageURL, Description, CategoryID, IsActive)
VALUES (N'Pokemon TCG Pack (Kỷ Niệm 25 Năm)', 50000, '/images/pack.jpg', N'Săn ngay thẻ bài Pikachu VMAX và Charizard Shiny!', 1, 1);

DECLARE @Box2 INT = SCOPE_IDENTITY();

-- Rank S
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, MarketValue) VALUES
(@Box2, N'Pikachu VMAX (Gigantamax)', 'S', 0.01, 5, 'https://images.pokemontcg.io/swsh4/44_hires.png', 5000000);

-- Rank A
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, MarketValue) VALUES 
(@Box2, N'Charizard Shiny', 'A', 0.045, 10, 'https://images.pokemontcg.io/swsh45/74_hires.png', 1000000),
(@Box2, N'Mewtwo GX', 'A', 0.045, 10, 'https://images.pokemontcg.io/sm35/78_hires.png', 1000000);

-- Rank B
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, MarketValue) VALUES 
(@Box2, N'Blastoise', 'B', 0.06, 50, 'https://dz3we2x72f7ol.cloudfront.net/expansions/151/en-us/SV3pt5_EN_9-2x.png', 500000),
(@Box2, N'Venusaur', 'B', 0.06, 50, 'https://dz3we2x72f7ol.cloudfront.net/expansions/151/en-us/SV3pt5_EN_3-2x.png', 500000),
(@Box2, N'Gengar', 'B', 0.06, 50, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/094.png', 500000),
(@Box2, N'Gyarados', 'B', 0.06, 50, 'https://images.pokemontcg.io/swsh1/35_hires.png', 500000),
(@Box2, N'Lucario', 'B', 0.06, 50, 'https://images.pokemontcg.io/swsh10/79_hires.png', 500000);

-- Rank C
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, MarketValue) VALUES 
(@Box2, N'Eevee', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/133.png', 200000),
(@Box2, N'Snorlax', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/143.png', 200000),
(@Box2, N'Jigglypuff', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/039.png', 200000),
(@Box2, N'Meowth', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/052.png', 200000),
(@Box2, N'Psyduck', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/054.png', 200000),
(@Box2, N'Machop', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/066.png', 200000),
(@Box2, N'Geodude', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/074.png', 200000);

-- Rank D
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, MarketValue) VALUES 
(@Box2, N'Caterpie', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/010.png', 50000),
(@Box2, N'Weedle', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/013.png', 50000),
(@Box2, N'Pidgey', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/016.png', 50000),
(@Box2, N'Rattata', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/019.png', 50000),
(@Box2, N'Spearow', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/021.png', 50000),
(@Box2, N'Zubat', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/041.png', 50000),
(@Box2, N'Oddish', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/043.png', 50000),
(@Box2, N'Diglett', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/050.png', 50000),
(@Box2, N'Poliwag', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/060.png', 50000),
(@Box2, N'Bellsprout', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/069.png', 50000),
(@Box2, N'Tentacool', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/072.png', 50000),
(@Box2, N'Slowpoke', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/079.png', 50000),
(@Box2, N'Magnemite', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/081.png', 50000),
(@Box2, N'Doduo', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/084.png', 50000),
(@Box2, N'Krabby', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/098.png', 50000);

-- -------------------------------------------------------------------------
-- BOX 3: ONE PIECE OP-05 (Full Ảnh Local .jpg/.webp chuẩn)
-- -------------------------------------------------------------------------
INSERT INTO BlindBoxes (BoxName, Description, Price, ImageURL, IsActive, CreatedAt, CategoryID)
VALUES (N'One Piece OP-05: Awakening of the New Era', N'Kỷ nguyên mới đã thức tỉnh! Săn ngay thẻ bài Monkey D. Luffy Gear 5 Manga Rare huyền thoại.', 80000, '/images/op-box.jpg', 1, GETDATE(), 1);

DECLARE @Box3 INT = SCOPE_IDENTITY();

INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, MarketValue) VALUES
(@Box3, N'Luffy Gear 5 (Manga Rare)', 'S', 0.01, 3, '/images/op-luffy-gear5.jpg', 15000000),
(@Box3, N'Trafalgar Law (Leader Parallel)', 'A', 0.045, 10, '/images/op-law.webp', 2000000),
(@Box3, N'Eustass "Captain" Kid (Leader)', 'A', 0.045, 10, '/images/op-kid.webp', 1800000),
(@Box3, N'Enel (God)', 'B', 0.06, 30, '/images/op-enel.webp', 500000),
(@Box3, N'Sabo (Revolutionary)', 'B', 0.06, 30, '/images/op-sabo.jpg', 450000),
(@Box3, N'Yamato', 'B', 0.06, 30, '/images/op-yamato.jpg', 600000),
(@Box3, N'Roronoa Zoro', 'B', 0.06, 30, '/images/op-zoro.jpeg', 550000),
(@Box3, N'Kaido (King of Beasts)', 'B', 0.06, 30, '/images/op-kaido.jpg', 500000),
(@Box3, N'Nami', 'C', 0.042, 100, '/images/op-nami.webp', 100000),
(@Box3, N'Sanji', 'C', 0.042, 100, '/images/op-sanji.jpg', 100000),
(@Box3, N'Tony Tony Chopper', 'C', 0.042, 100, '/images/op-chopper.jpg', 100000),
(@Box3, N'Nico Robin', 'C', 0.042, 100, '/images/op-robin.jpg', 100000),
(@Box3, N'Franky', 'C', 0.042, 100, '/images/op-franky.jpg', 100000),
(@Box3, N'Brook', 'C', 0.042, 100, '/images/op-brook.png', 100000),
(@Box3, N'Jinbe', 'C', 0.042, 100, '/images/op-jinbe.jpg', 100000),
(@Box3, N'Marine Soldier', 'D', 0.02, 500, '/images/op-marine.jpg', 20000),
(@Box3, N'Pirate Crew', 'D', 0.02, 500, '/images/op-crew.jpg', 20000),
(@Box3, N'Buggy', 'D', 0.02, 500, '/images/op-buggy.jpg', 20000),
(@Box3, N'Alvida', 'D', 0.02, 500, '/images/op-alvida.jpg', 20000),
(@Box3, N'Koby', 'D', 0.02, 500, '/images/op-koby.webp', 20000),
(@Box3, N'Helmeppo', 'D', 0.02, 500, '/images/op-helmeppo.jpg', 20000),
(@Box3, N'Morgan', 'D', 0.02, 500, '/images/op-morgan.jpg', 20000),
(@Box3, N'Mohji', 'D', 0.02, 500, '/images/op-mohji.jpg', 20000),
(@Box3, N'Cabaji', 'D', 0.02, 500, '/images/op-cabaji.webp', 20000),
(@Box3, N'Gaimon', 'D', 0.02, 500, '/images/op-gaimon.jpg', 20000),
(@Box3, N'Kuro', 'D', 0.02, 500, '/images/op-kuro.jpg', 20000),
(@Box3, N'Jango', 'D', 0.02, 500, '/images/op-jango.jpg', 20000),
(@Box3, N'Sham', 'D', 0.02, 500, '/images/op-sham.jpg', 20000),
(@Box3, N'Buchi', 'D', 0.02, 500, '/images/op-buchi.jpg', 20000),
(@Box3, N'Don Krieg', 'D', 0.02, 500, '/images/op-donkrieg.jpg', 20000);

-- -------------------------------------------------------------------------
-- BOX 4: GENSHIN IMPACT (Full Ảnh Local .jpg/.webp chuẩn)
-- -------------------------------------------------------------------------
INSERT INTO BlindBoxes (BoxName, Description, Price, ImageURL, IsActive, CreatedAt, CategoryID)
VALUES (N'Genshin Impact: Metal Card Collection', N'Bộ sưu tập thẻ kim loại nhân vật Genshin Impact. Cơ hội trúng Lôi Thần Raiden Shogun!', 45000, '/images/gs-box.jpg', 1, GETDATE(), 2);

DECLARE @Box4 INT = SCOPE_IDENTITY();

INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, MarketValue) VALUES
(@Box4, N'Raiden Shogun (Baal)', 'S', 0.01, 5, '/images/gs-raiden.webp', 3000000),
(@Box4, N'Zhongli (Rex Lapis)', 'A', 0.045, 15, '/images/gs-zhongli.webp', 1500000),
(@Box4, N'Nahida (Kusanali)', 'A', 0.045, 15, '/images/gs-nahida.webp', 1500000),
(@Box4, N'Hu Tao', 'B', 0.06, 40, '/images/gs-hutao.webp', 800000),
(@Box4, N'Kamisato Ayaka', 'B', 0.06, 40, '/images/gs-ayaka.webp', 800000),
(@Box4, N'Ganyu', 'B', 0.06, 40, '/images/gs-ganyu.webp', 800000),
(@Box4, N'Xiao', 'B', 0.06, 40, '/images/gs-xiao.jpg', 800000),
(@Box4, N'Kaedehara Kazuha', 'B', 0.06, 40, '/images/gs-kazuha.webp', 800000),
(@Box4, N'Diluc', 'C', 0.042, 80, '/images/gs-diluc.jpg', 300000),
(@Box4, N'Keqing', 'C', 0.042, 80, '/images/gs-keqing.jpg', 300000),
(@Box4, N'Mona', 'C', 0.042, 80, '/images/gs-mona.webp', 300000),
(@Box4, N'Qiqi', 'C', 0.042, 80, '/images/gs-qiqi.webp', 300000),
(@Box4, N'Jean', 'C', 0.042, 80, '/images/gs-jean.jpg', 300000),
(@Box4, N'Tighnari', 'C', 0.042, 80, '/images/gs-tighnari.jpg', 300000),
(@Box4, N'Dehya', 'C', 0.042, 80, '/images/gs-dehya.jpg', 300000),
(@Box4, N'Bennett', 'D', 0.02, 300, '/images/gs-bennett.jpg', 50000),
(@Box4, N'Xiangling', 'D', 0.02, 300, '/images/gs-xiangling.jpg', 50000),
(@Box4, N'Xingqiu', 'D', 0.02, 300, '/images/gs-xingqiu.webp', 50000),
(@Box4, N'Fischl', 'D', 0.02, 300, '/images/gs-fischl.jpg', 50000),
(@Box4, N'Barbara', 'D', 0.02, 300, '/images/gs-barbara.webp', 50000),
(@Box4, N'Noelle', 'D', 0.02, 300, '/images/gs-noelle.webp', 50000),
(@Box4, N'Sucrose', 'D', 0.02, 300, '/images/gs-sucrose.jpg', 50000),
(@Box4, N'Razor', 'D', 0.02, 300, '/images/gs-razor.jpg', 50000),
(@Box4, N'Lisa', 'D', 0.02, 300, '/images/gs-lisa.jpg', 50000),
(@Box4, N'Kaeya', 'D', 0.02, 300, '/images/gs-kaeya.jpg', 50000),
(@Box4, N'Amber', 'D', 0.02, 300, '/images/gs-amber.webp', 50000),
(@Box4, N'Chongyun', 'D', 0.02, 300, '/images/gs-chongyun.webp', 50000),
(@Box4, N'Beidou', 'D', 0.02, 300, '/images/gs-beidou.jpg', 50000),
(@Box4, N'Ningguang', 'D', 0.02, 300, '/images/gs-ningguang.jpg', 50000),
(@Box4, N'Yanfei', 'D', 0.02, 300, '/images/gs-yanfei.jpg', 50000);

PRINT N'=== KHỞI TẠO DATABASE HOÀN TẤT ===';
PRINT N'Hãy đảm bảo file ảnh trong src/main/resources/static/images khớp chính xác đuôi (.jpg, .webp, .png) như trong code!';
GO

use BlindBoxDB;
-- Cập nhật giá cho 4 Box đầu tiên (BoxID 1, 2, 3, 4)

-- Hộp 1: Hộp Thường (Tăng lên 150k)
UPDATE BlindBoxes 
SET Price = 150000 
WHERE BoxID = 1;

-- Hộp 2: Hộp Hiếm (Tăng lên 500k)
UPDATE BlindBoxes 
SET Price = 200000 
WHERE BoxID = 2;

-- Hộp 3: Hộp Sử Thi (Tăng lên 1 triệu)
UPDATE BlindBoxes 
SET Price = 175000 
WHERE BoxID = 3;

-- Hộp 4: Hộp Huyền Thoại (Tăng lên 2.5 triệu - Cho đại gia mở)
UPDATE BlindBoxes 
SET Price = 150000 
WHERE BoxID = 4;

USE BlindBoxDB; -- Thay tên DB của bạn vào đây nếu cần
GO

-- 1. Thêm ReceiverName (Nếu chưa có)
IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'ReceiverName' AND Object_ID = Object_ID(N'Orders'))
BEGIN
    ALTER TABLE Orders ADD ReceiverName NVARCHAR(255);
    PRINT 'Da them cot ReceiverName';
END

-- 2. Thêm PhoneNumber
IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'PhoneNumber' AND Object_ID = Object_ID(N'Orders'))
BEGIN
    ALTER TABLE Orders ADD PhoneNumber VARCHAR(20);
    PRINT 'Da them cot PhoneNumber';
END

-- 3. Thêm ShippingAddress
IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'ShippingAddress' AND Object_ID = Object_ID(N'Orders'))
BEGIN
    ALTER TABLE Orders ADD ShippingAddress NVARCHAR(MAX);
    PRINT 'Da them cot ShippingAddress';
END

-- 4. Thêm OrderStatus
IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'OrderStatus' AND Object_ID = Object_ID(N'Orders'))
BEGIN
    ALTER TABLE Orders ADD OrderStatus VARCHAR(50);
    PRINT 'Da them cot OrderStatus';
END

-- 5. Thêm PaymentMethod
IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'PaymentMethod' AND Object_ID = Object_ID(N'Orders'))
BEGIN
    ALTER TABLE Orders ADD PaymentMethod VARCHAR(50);
    PRINT 'Da them cot PaymentMethod';
END

-- 6. Thêm PaymentStatus
IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'PaymentStatus' AND Object_ID = Object_ID(N'Orders'))
BEGIN
    ALTER TABLE Orders ADD PaymentStatus VARCHAR(50);
    PRINT 'Da them cot PaymentStatus';
END

-- 7. Thêm DeliveryDate
IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'DeliveryDate' AND Object_ID = Object_ID(N'Orders'))
BEGIN
    ALTER TABLE Orders ADD DeliveryDate DATETIME;
    PRINT 'Da them cot DeliveryDate';
END

PRINT '--- CAP NHAT HOAN TAT ---';
GO

-- Thêm cột CreatedAt nếu chưa có
IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'CreatedAt' AND Object_ID = Object_ID(N'UserInventory'))
BEGIN
    ALTER TABLE UserInventory ADD CreatedAt DATETIME DEFAULT GETDATE();
    
    -- Cập nhật dữ liệu cũ (lấy giá trị từ ObtainedDate sang CreatedAt)
    EXEC('UPDATE UserInventory SET CreatedAt = ObtainedDate WHERE CreatedAt IS NULL');
END

USE BlindBoxDB;
GO
ALTER TABLE Orders ADD Note NVARCHAR(MAX);
GO