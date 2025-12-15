USE master;
GO

-- 1. XÓA DB CŨ NẾU CÓ (Làm sạch)
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'BlindBoxDB')
BEGIN
    ALTER DATABASE BlindBoxDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE BlindBoxDB;
END
GO

-- 2. TẠO DB MỚI
CREATE DATABASE BlindBoxDB;
GO

USE BlindBoxDB;
GO

-- =========================================================================
-- PHẦN 1: NGƯỜI DÙNG & HỆ THỐNG
-- =========================================================================

-- Bảng Users
CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(50) NOT NULL UNIQUE,
    PasswordHash NVARCHAR(255) NOT NULL,
    Email NVARCHAR(100), -- Có thể để trống, nhưng nếu điền thì nên Unique (xử lý ở code)
    FullName NVARCHAR(100),
    Address NVARCHAR(MAX),      -- Địa chỉ mặc định
    PhoneNumber NVARCHAR(20),   -- Số điện thoại
    WalletBalance DECIMAL(18, 2) DEFAULT 0,
    IsAdmin BIT DEFAULT 0,      -- 1: Admin, 0: User
    CreatedAt DATETIME DEFAULT GETDATE(),
    
    CONSTRAINT CK_Wallet_NonNegative CHECK (WalletBalance >= 0)
);

-- Bảng Transactions (Lịch sử biến động số dư)
CREATE TABLE Transactions (
    TransactionID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    Amount DECIMAL(18, 2) NOT NULL, -- Dương: Nạp/Bán, Âm: Mua
    TransactionType NVARCHAR(50),   -- DEPOSIT, BUY_BOX, SELL_BACK, ORDER_PAYMENT, ADMIN_DEPOSIT
    Description NVARCHAR(255),
    TransactionDate DATETIME DEFAULT GETDATE()
);

-- Bảng News (Tin tức / Blog)
CREATE TABLE News (
    NewsID INT IDENTITY(1,1) PRIMARY KEY,
    Title NVARCHAR(200) NOT NULL,
    Content NVARCHAR(MAX),
    Thumbnail NVARCHAR(MAX),
    PublishedAt DATETIME DEFAULT GETDATE()
);

-- =========================================================================
-- PHẦN 2: GACHA BLIND BOX (LÕI GAME)
-- =========================================================================

-- Bảng BlindBoxes (Hộp mù)
CREATE TABLE BlindBoxes (
    BoxID INT IDENTITY(1,1) PRIMARY KEY,
    BoxName NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX),
    Price DECIMAL(18, 2) NOT NULL,
    ImageURL NVARCHAR(MAX),
    IsActive BIT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- Bảng BoxItems (Vật phẩm trong hộp)
CREATE TABLE BoxItems (
    ItemID INT IDENTITY(1,1) PRIMARY KEY,
    BoxID INT FOREIGN KEY REFERENCES BlindBoxes(BoxID) ON DELETE CASCADE,
    ItemName NVARCHAR(255) NOT NULL,
    ImageURL NVARCHAR(MAX),
    
    -- Chỉ số Gacha
    RarityLevel VARCHAR(10) NOT NULL, -- S, A, B, C, D
    Probability FLOAT DEFAULT 0,      -- Tỉ lệ (0.01 = 1%)
    MarketValue DECIMAL(18, 2) DEFAULT 0, -- Giá trị thực (để bán lại)
    
    StockQuantity INT DEFAULT 0,
    IsHidden BIT DEFAULT 1,      -- Item bí mật
    IsPityReward BIT DEFAULT 0   -- Item thưởng bảo hiểm
);

-- Bảng UserInventory (Kho đồ của người chơi)
CREATE TABLE UserInventory (
    InventoryID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    ItemID INT FOREIGN KEY REFERENCES BoxItems(ItemID),
    ObtainedDate DATETIME DEFAULT GETDATE(),
    Status NVARCHAR(50) DEFAULT 'IN_STORAGE', -- IN_STORAGE, SOLD_BACK, REQUESTED_SHIP
    SoldPrice DECIMAL(18, 2) DEFAULT 0
);

-- Bảng UserPityStats (Bảo hiểm đen đủi)
CREATE TABLE UserPityStats (
    StatID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    BoxID INT FOREIGN KEY REFERENCES BlindBoxes(BoxID),
    SpinsWithoutS INT DEFAULT 0, -- Số lần quay chưa ra S
    LastSRewardDate DATETIME
);

-- Bảng ShipmentRequests (Yêu cầu ship kho đồ về nhà)
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

-- Chi tiết yêu cầu ship (Ship món nào trong kho)
CREATE TABLE ShipmentDetails (
    DetailID INT IDENTITY(1,1) PRIMARY KEY,
    ShipmentID INT FOREIGN KEY REFERENCES ShipmentRequests(ShipmentID) ON DELETE CASCADE,
    InventoryID INT FOREIGN KEY REFERENCES UserInventory(InventoryID)
);

-- =========================================================================
-- PHẦN 3: E-COMMERCE (MUA SẮM TRUYỀN THỐNG)
-- =========================================================================

-- Bảng Categories (Danh mục sản phẩm thường)
CREATE TABLE Categories (
    CategoryID INT IDENTITY(1,1) PRIMARY KEY,
    CategoryName NVARCHAR(100) NOT NULL,
    Description NVARCHAR(MAX)
);

-- Bảng Products (Sản phẩm thường - Không phải Blind Box)
CREATE TABLE Products (
    ProductID INT IDENTITY(1,1) PRIMARY KEY,
    CategoryID INT FOREIGN KEY REFERENCES Categories(CategoryID),
    ProductName NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX),
    Price DECIMAL(18, 2) NOT NULL,
    StockQuantity INT DEFAULT 0,
    ImageURL NVARCHAR(MAX),
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- Bảng Carts (Giỏ hàng)
CREATE TABLE Carts (
    CartID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- Chi tiết giỏ hàng (Chứa cả BlindBox và Product thường)
CREATE TABLE CartItems (
    CartItemID INT IDENTITY(1,1) PRIMARY KEY,
    CartID INT FOREIGN KEY REFERENCES Carts(CartID) ON DELETE CASCADE,
    ProductID INT FOREIGN KEY REFERENCES Products(ProductID), -- Mua lẻ
    BoxID INT FOREIGN KEY REFERENCES BlindBoxes(BoxID),       -- Mua hộp
    Quantity INT DEFAULT 1,
    AddedAt DATETIME DEFAULT GETDATE()
);

-- Bảng Orders (Đơn hàng thanh toán)
CREATE TABLE Orders (
    OrderID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    OrderDate DATETIME DEFAULT GETDATE(),
    TotalAmount DECIMAL(18, 2) NOT NULL,
    
    ReceiverName NVARCHAR(100),
    PhoneNumber NVARCHAR(20),
    ShippingAddress NVARCHAR(MAX),
    
    PaymentMethod NVARCHAR(50), -- COD, WALLET
    PaymentStatus NVARCHAR(50), -- PAID, UNPAID
    OrderStatus NVARCHAR(50) DEFAULT 'PENDING' -- PENDING, COMPLETED, CANCELLED
);

CREATE TABLE OrderDetails (
    OrderDetailID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT FOREIGN KEY REFERENCES Orders(OrderID) ON DELETE CASCADE,
    ProductID INT FOREIGN KEY REFERENCES Products(ProductID),
    BoxID INT FOREIGN KEY REFERENCES BlindBoxes(BoxID),
    Quantity INT NOT NULL,
    PriceAtTime DECIMAL(18, 2) NOT NULL
);

-- Bảng Reviews (Đánh giá)
CREATE TABLE Reviews (
    ReviewID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    ProductID INT FOREIGN KEY REFERENCES Products(ProductID),
    BoxID INT FOREIGN KEY REFERENCES BlindBoxes(BoxID),
    Rating INT CHECK (Rating >= 1 AND Rating <= 5),
    Comment NVARCHAR(MAX),
    ReviewDate DATETIME DEFAULT GETDATE()
);

GO

-- =========================================================================
-- PHẦN 4: DỮ LIỆU MẪU (SEED DATA) - CHẠY 1 LẦN DÙNG LUÔN
-- =========================================================================

-- 1. Tạo Tài Khoản Admin & User
INSERT INTO Users (Username, PasswordHash, FullName, WalletBalance, IsAdmin, Email, CreatedAt)
VALUES 
('admin', '123456', N'Quản Trị Viên', 0, 1, 'admin@blindbox.com', GETDATE()),
('vinh_test', '123456', N'Nguyễn Xuân Vinh', 5000000, 0, 'vinh@test.com', GETDATE());

-- 2. Tạo Hộp Gấu Bông
INSERT INTO BlindBoxes (BoxName, Price, ImageURL, Description, IsActive, CreatedAt)
VALUES (N'Hộp Bí Mật Plushie (Plushie Box)', 100000, '/images/box.jpg', N'Sưu tập trọn bộ 30 gấu bông siêu cấp đáng yêu! Cơ hội nhận gấu khổng lồ.', 1, GETDATE());

DECLARE @BoxID INT = SCOPE_IDENTITY(); -- Lấy ID hộp vừa tạo (thường là 1)

-- 3. Tạo 30 Item Gấu Bông (S:1, A:2, B:5, C:7, D:15)
-- Hạng S (1 con - 1%)
INSERT INTO BoxItems (BoxID, ItemName, ImageURL, RarityLevel, Probability, MarketValue, StockQuantity, IsPityReward)
VALUES (@BoxID, N'S Plushie #1', '/images/rank_s.jpg', 'S', 0.01, 2000000, 5, 1);

-- Hạng A (2 con - 2%/con)
DECLARE @i INT = 1;
WHILE @i <= 2 BEGIN
    INSERT INTO BoxItems (BoxID, ItemName, ImageURL, RarityLevel, Probability, MarketValue, StockQuantity, IsPityReward)
    VALUES (@BoxID, N'A Plushie #' + CAST(@i AS NVARCHAR), '/images/rank_a' + (CASE WHEN @i=1 THEN '' ELSE CAST(@i AS NVARCHAR) END) + '.jpg', 'A', 0.02, 500000, 20, 0);
    SET @i = @i + 1;
END

-- Hạng B (5 con - 3%/con)
SET @i = 1;
WHILE @i <= 5 BEGIN
    INSERT INTO BoxItems (BoxID, ItemName, ImageURL, RarityLevel, Probability, MarketValue, StockQuantity, IsPityReward)
    VALUES (@BoxID, N'B Plushie #' + CAST(@i AS NVARCHAR), '/images/rank_b' + (CASE WHEN @i=1 THEN '' ELSE CAST(@i AS NVARCHAR) END) + '.jpg', 'B', 0.03, 150000, 50, 0);
    SET @i = @i + 1;
END

-- Hạng C (7 con - ~4.28%/con)
SET @i = 1;
WHILE @i <= 7 BEGIN
    INSERT INTO BoxItems (BoxID, ItemName, ImageURL, RarityLevel, Probability, MarketValue, StockQuantity, IsPityReward)
    VALUES (@BoxID, N'C Plushie #' + CAST(@i AS NVARCHAR), '/images/rank_c' + (CASE WHEN @i=1 THEN '' ELSE CAST(@i AS NVARCHAR) END) + '.jpg', 'C', 0.0428, 80000, 100, 0);
    SET @i = @i + 1;
END

-- Hạng D (15 con - ~3.33%/con)
SET @i = 1;
WHILE @i <= 15 BEGIN
    INSERT INTO BoxItems (BoxID, ItemName, ImageURL, RarityLevel, Probability, MarketValue, StockQuantity, IsPityReward)
    VALUES (@BoxID, N'D Plushie #' + CAST(@i AS NVARCHAR), '/images/rank_d' + (CASE WHEN @i=1 THEN '' ELSE CAST(@i AS NVARCHAR) END) + '.jpg', 'D', 0.0333, 20000, 200, 0);
    SET @i = @i + 1;
END

GO

USE BlindBoxDB;
GO

-- 1. Thêm cột CategoryID vào bảng BlindBoxes
ALTER TABLE BlindBoxes
ADD CategoryID INT;
GO

-- 2. Tạo khóa ngoại liên kết BlindBoxes với Categories
ALTER TABLE BlindBoxes
ADD CONSTRAINT FK_BlindBoxes_Categories
FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID);
GO

USE BlindBoxDB;
GO

-- 1. Bảng Banners (Quảng cáo chạy slide)
CREATE TABLE Banners (
    BannerID INT IDENTITY(1,1) PRIMARY KEY,
    Title NVARCHAR(100),
    ImageURL NVARCHAR(MAX) NOT NULL,
    LinkUrl NVARCHAR(MAX), -- Link khi bấm vào banner (VD: trỏ đến hộp mù mới)
    DisplayOrder INT DEFAULT 0, -- Thứ tự hiển thị
    IsActive BIT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 2. Bảng Feedbacks (Phản hồi & Khiếu nại từ khách)
CREATE TABLE Feedbacks (
    FeedbackID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID), -- Null nếu khách vãng lai
    CustomerName NVARCHAR(100), -- Tên người gửi
    Email NVARCHAR(100),
    Subject NVARCHAR(200), -- Chủ đề (VD: Khiếu nại nạp tiền)
    Content NVARCHAR(MAX),
    Status NVARCHAR(50) DEFAULT 'PENDING', -- PENDING, PROCESSING, RESOLVED
    SentAt DATETIME DEFAULT GETDATE(),
    ReplyContent NVARCHAR(MAX) -- Nội dung Admin trả lời
);
GO

USE BlindBoxDB;
GO

-- Thêm cột ImageURL và IsActive vào bảng Categories
ALTER TABLE Categories ADD ImageURL NVARCHAR(MAX);
ALTER TABLE Categories ADD IsActive BIT DEFAULT 1;
GO

-- 1. TẠO HỘP POKEMON PACK
DECLARE @NewBoxID INT;

INSERT INTO BlindBoxes (BoxName, Description, Price, ImageURL, IsActive, CreatedAt, CategoryID)
VALUES (
    N'Pokemon TCG Pack (Kỷ Niệm 25 Năm)', 
    N'Săn ngay thẻ bài Pokemon huyền thoại! Cơ hội trúng thẻ Pikachu Illustrator cực hiếm.', 
    50000, -- Giá 50k
    'https://assets.pokemon.com/assets/cms2/img/trading-card-game/_tiles/sv04/sv04-landing-169-en.jpg', -- Ảnh hộp
    1, -- Đang bán
    GETDATE(),
    NULL -- Hoặc điền ID danh mục Anime nếu có (ví dụ: 1)
);

-- Lấy ID của hộp vừa tạo
SET @NewBoxID = SCOPE_IDENTITY();

-- 2. THÊM 30 THẺ BÀI (Theo cấu trúc: 1S - 2A - 5B - 7C - 15D)
-- Tự động tính toán tỉ lệ rơi (Probability) sao cho tổng ~ 100%

-- --- RANK S (1 Thẻ - Tỉ lệ 1%) ---
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES (@NewBoxID, N'Pikachu Illustrator (Holo)', 'S', 0.01, 5, 'https://i.ebayimg.com/images/g/c4AAAOSw~HBaZn6~/s-l1200.jpg', 0, 5000000);

-- --- RANK A (2 Thẻ - Tỉ lệ 4.5% mỗi thẻ -> Tổng 9%) ---
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES 
(@NewBoxID, N'Charizard VMAX', 'A', 0.045, 10, 'https://m.media-amazon.com/images/I/71wF+3+1VfL.jpg', 0, 1000000),
(@NewBoxID, N'Mewtwo GX', 'A', 0.045, 10, 'https://m.media-amazon.com/images/I/71Xq5KqXNUL._AC_UF894,1000_QL80_.jpg', 0, 1000000);

-- --- RANK B (5 Thẻ - Tỉ lệ 6% mỗi thẻ -> Tổng 30%) ---
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES 
(@NewBoxID, N'Blastoise', 'B', 0.06, 50, 'https://dz3we2x72f7ol.cloudfront.net/expansions/151/en-us/SV3pt5_EN_9-2x.png', 0, 500000),
(@NewBoxID, N'Venusaur', 'B', 0.06, 50, 'https://dz3we2x72f7ol.cloudfront.net/expansions/151/en-us/SV3pt5_EN_3-2x.png', 0, 500000),
(@NewBoxID, N'Gengar', 'B', 0.06, 50, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/094.png', 0, 500000),
(@NewBoxID, N'Gyarados', 'B', 0.06, 50, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/130.png', 0, 500000),
(@NewBoxID, N'Lucario', 'B', 0.06, 50, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/448.png', 0, 500000);

-- --- RANK C (7 Thẻ - Tỉ lệ 4.2% mỗi thẻ -> Tổng ~30%) ---
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES 
(@NewBoxID, N'Eevee', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/133.png', 0, 200000),
(@NewBoxID, N'Snorlax', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/143.png', 0, 200000),
(@NewBoxID, N'Jigglypuff', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/039.png', 0, 200000),
(@NewBoxID, N'Meowth', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/052.png', 0, 200000),
(@NewBoxID, N'Psyduck', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/054.png', 0, 200000),
(@NewBoxID, N'Machop', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/066.png', 0, 200000),
(@NewBoxID, N'Geodude', 'C', 0.042, 100, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/074.png', 0, 200000);

-- --- RANK D (15 Thẻ - Tỉ lệ 2% mỗi thẻ -> Tổng 30%) ---
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES 
(@NewBoxID, N'Caterpie', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/010.png', 0, 50000),
(@NewBoxID, N'Weedle', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/013.png', 0, 50000),
(@NewBoxID, N'Pidgey', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/016.png', 0, 50000),
(@NewBoxID, N'Rattata', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/019.png', 0, 50000),
(@NewBoxID, N'Spearow', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/021.png', 0, 50000),
(@NewBoxID, N'Zubat', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/041.png', 0, 50000),
(@NewBoxID, N'Oddish', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/043.png', 0, 50000),
(@NewBoxID, N'Diglett', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/050.png', 0, 50000),
(@NewBoxID, N'Poliwag', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/060.png', 0, 50000),
(@NewBoxID, N'Bellsprout', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/069.png', 0, 50000),
(@NewBoxID, N'Tentacool', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/072.png', 0, 50000),
(@NewBoxID, N'Slowpoke', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/079.png', 0, 50000),
(@NewBoxID, N'Magnemite', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/081.png', 0, 50000),
(@NewBoxID, N'Doduo', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/084.png', 0, 50000),
(@NewBoxID, N'Krabby', 'D', 0.02, 500, 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/098.png', 0, 50000);

PRINT N'Đã thêm thành công Hộp Pokemon và 30 thẻ bài!';

use BlindBoxDB;
-- 1. Sửa ảnh Hộp (Box) thành ảnh bìa Pokemon đẹp hơn
UPDATE BlindBoxes 
SET ImageURL = 'https://assets.pokemon.com/assets/cms2/img/trading-card-game/_tiles/sv03/sv03-launch-169-en.jpg'
WHERE BoxName LIKE N'%Pokemon TCG Pack%';

-- 2. Sửa ảnh Pikachu Illustrator (Holo) - Rank S
UPDATE BoxItems 
SET ImageURL = 'https://images.pokemontcg.io/cel25c/2_hires.png' -- Ảnh Pikachu Classic kỷ niệm 25 năm (Cực nét)
WHERE ItemName = N'Pikachu Illustrator (Holo)';

-- 3. Sửa ảnh Charizard VMAX - Rank A
UPDATE BoxItems 
SET ImageURL = 'https://images.pokemontcg.io/swsh3/20_hires.png' -- Charizard VMAX rực lửa
WHERE ItemName = N'Charizard VMAX';

-- 4. Sửa ảnh Mewtwo GX - Rank A
UPDATE BoxItems 
SET ImageURL = 'https://images.pokemontcg.io/sm35/39_hires.png' -- Mewtwo GX huyền bí
WHERE ItemName = N'Mewtwo GX';

-- (Tùy chọn) Sửa thêm mấy con Rank B cho đẹp luôn nếu cần
UPDATE BoxItems SET ImageURL = 'https://images.pokemontcg.io/swsh1/35_hires.png' WHERE ItemName = N'Gyarados';
UPDATE BoxItems SET ImageURL = 'https://images.pokemontcg.io/swsh10/79_hires.png' WHERE ItemName = N'Lucario';

PRINT N'Đã cập nhật lại toàn bộ link ảnh Pokemon xịn!';

-- 1. Cập nhật ảnh Hộp (Box) - Lấy ảnh Hộp Celebrations 25th (Đẹp, Sang)
UPDATE BlindBoxes 
SET ImageURL = 'https://m.media-amazon.com/images/I/91Q+r8A7XLL._AC_SL1500_.jpg'
WHERE BoxName LIKE N'%Pokemon%';

-- 2. Đổi Pikachu "xấu" thành Pikachu VMAX (Giga Chu) Siêu Ngầu
-- Con này là Vivid Voltage VMAX, nhìn sấm sét rất lực
UPDATE BoxItems 
SET 
    ItemName = N'Pikachu VMAX (Gigantamax)', 
    ImageURL = 'https://images.pokemontcg.io/swsh4/44_hires.png' 
WHERE ItemName LIKE N'%Pikachu%';

-- 3. Cập nhật Charizard VMAX (Dạng Shiny hoặc Full Art ngầu hơn)
UPDATE BoxItems 
SET ImageURL = 'https://images.pokemontcg.io/swsh45/74_hires.png' -- Charizard Shiny đen cực hiếm
WHERE ItemName LIKE N'%Charizard%';

-- 4. Cập nhật Mewtwo GX (Dạng Mewtube thí nghiệm - Cực đẹp)
UPDATE BoxItems 
SET ImageURL = 'https://images.pokemontcg.io/sm35/78_hires.png' -- Mewtwo trong ống nghiệm (Shining Legends)
WHERE ItemName LIKE N'%Mewtwo%';

PRINT N'Đã cập nhật bộ ảnh Pokemon Pack mới: Box xịn, Pikachu VMAX, Charizard Shiny!';
-- Thay 'ten-file-anh-cua-ban.jpg' bằng tên file thực tế bạn đã chép vào thư mục
UPDATE BlindBoxes 
SET ImageURL = '/images/pack.jpg'
WHERE BoxName LIKE N'%Pokemon%';

-- =============================================
-- 1. BOX: ONE PIECE CARD GAME (OP-05)
-- =============================================
DECLARE @OPBoxID INT;

INSERT INTO BlindBoxes (BoxName, Description, Price, ImageURL, IsActive, CreatedAt, CategoryID)
VALUES (
    N'One Piece OP-05: Awakening of the New Era', 
    N'Kỷ nguyên mới đã thức tỉnh! Săn ngay thẻ bài Monkey D. Luffy Gear 5 Manga Rare huyền thoại.', 
    80000, -- Giá 80k
    'https://m.media-amazon.com/images/I/81+Xy-c1cAL._AC_SL1500_.jpg', -- Ảnh Box
    1, 
    GETDATE(),
    NULL 
);

SET @OPBoxID = SCOPE_IDENTITY();

-- S RANK (1 Thẻ - Luffy Gear 5 Manga Art - Cực hiếm)
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES (@OPBoxID, N'Luffy Gear 5 (Manga Rare)', 'S', 0.01, 3, 'https://i.ebayimg.com/images/g/2sAAAOSwylplVw2e/s-l1200.jpg', 0, 15000000);

-- A RANK (2 Thẻ - Leaders Alt Art)
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES 
(@OPBoxID, N'Trafalgar Law (Leader Parallel)', 'A', 0.045, 10, 'https://i.ebayimg.com/images/g/Y~QAAOSw~CRlG~tx/s-l1600.jpg', 0, 2000000),
(@OPBoxID, N'Eustass "Captain" Kid (Leader)', 'A', 0.045, 10, 'https://i.ebayimg.com/images/g/0~sAAOSw9oplG~uD/s-l1600.jpg', 0, 1800000);

-- B RANK (5 Thẻ - Super Rares)
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES 
(@OPBoxID, N'Enel (God)', 'B', 0.06, 30, 'https://m.media-amazon.com/images/I/61y8Q-uP-IL._AC_UF894,1000_QL80_.jpg', 0, 500000),
(@OPBoxID, N'Sabo (Revolutionary)', 'B', 0.06, 30, 'https://m.media-amazon.com/images/I/61+9+8+4+SL.jpg', 0, 450000),
(@OPBoxID, N'Yamato', 'B', 0.06, 30, 'https://m.media-amazon.com/images/I/71X-1-3-1BL.jpg', 0, 600000),
(@OPBoxID, N'Roronoa Zoro', 'B', 0.06, 30, 'https://m.media-amazon.com/images/I/51+1+2+3+JL.jpg', 0, 550000),
(@OPBoxID, N'Kaido (King of Beasts)', 'B', 0.06, 30, 'https://m.media-amazon.com/images/I/61+1+2+3+KL.jpg', 0, 500000);

-- C RANK (7 Thẻ - Rares)
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES 
(@OPBoxID, N'Nami', 'C', 0.042, 100, 'https://m.media-amazon.com/images/I/51+1+2+3+ML.jpg', 0, 100000),
(@OPBoxID, N'Sanji', 'C', 0.042, 100, 'https://m.media-amazon.com/images/I/51+1+2+3+NL.jpg', 0, 100000),
(@OPBoxID, N'Tony Tony Chopper', 'C', 0.042, 100, 'https://m.media-amazon.com/images/I/51+1+2+3+OL.jpg', 0, 100000),
(@OPBoxID, N'Nico Robin', 'C', 0.042, 100, 'https://m.media-amazon.com/images/I/51+1+2+3+PL.jpg', 0, 100000),
(@OPBoxID, N'Franky', 'C', 0.042, 100, 'https://m.media-amazon.com/images/I/51+1+2+3+QL.jpg', 0, 100000),
(@OPBoxID, N'Brook', 'C', 0.042, 100, 'https://m.media-amazon.com/images/I/51+1+2+3+RL.jpg', 0, 100000),
(@OPBoxID, N'Jinbe', 'C', 0.042, 100, 'https://m.media-amazon.com/images/I/51+1+2+3+SL.jpg', 0, 100000);

-- D RANK (15 Thẻ - Commons)
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES 
(@OPBoxID, N'Marine Soldier', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+TL.jpg', 0, 20000),
(@OPBoxID, N'Pirate Crew', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+UL.jpg', 0, 20000),
(@OPBoxID, N'Buggy', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+VL.jpg', 0, 20000),
(@OPBoxID, N'Alvida', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+WL.jpg', 0, 20000),
(@OPBoxID, N'Koby', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+XL.jpg', 0, 20000),
(@OPBoxID, N'Helmeppo', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+YL.jpg', 0, 20000),
(@OPBoxID, N'Morgan', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+ZL.jpg', 0, 20000),
(@OPBoxID, N'Mohji', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+0L.jpg', 0, 20000),
(@OPBoxID, N'Cabaji', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+1L.jpg', 0, 20000),
(@OPBoxID, N'Gaimon', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+2L.jpg', 0, 20000),
(@OPBoxID, N'Kuro', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+3L.jpg', 0, 20000),
(@OPBoxID, N'Jango', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+4L.jpg', 0, 20000),
(@OPBoxID, N'Sham', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+5L.jpg', 0, 20000),
(@OPBoxID, N'Buchi', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+6L.jpg', 0, 20000),
(@OPBoxID, N'Don Krieg', 'D', 0.02, 500, 'https://m.media-amazon.com/images/I/41+1+2+3+7L.jpg', 0, 20000);

PRINT N'Đã thêm xong Box One Piece!';

-- =============================================
-- 2. BOX: GENSHIN IMPACT METAL COLLECTION
-- =============================================
DECLARE @GenshinBoxID INT;

INSERT INTO BlindBoxes (BoxName, Description, Price, ImageURL, IsActive, CreatedAt, CategoryID)
VALUES (
    N'Genshin Impact: Metal Card Collection', 
    N'Bộ sưu tập thẻ kim loại nhân vật Genshin Impact. Cơ hội trúng Lôi Thần Raiden Shogun!', 
    45000, -- Giá 45k
    'https://m.media-amazon.com/images/I/81gC7q+y+KL.jpg', -- Ảnh Box
    1, 
    GETDATE(),
    NULL
);

SET @GenshinBoxID = SCOPE_IDENTITY();

-- S RANK (1 Thẻ - Raiden Shogun)
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES (@GenshinBoxID, N'Raiden Shogun (Baal)', 'S', 0.01, 5, 'https://m.media-amazon.com/images/I/61tC-t+J+SL._AC_SL1000_.jpg', 0, 3000000);

-- A RANK (2 Thẻ - Zhongli, Nahida)
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES 
(@GenshinBoxID, N'Zhongli (Rex Lapis)', 'A', 0.045, 15, 'https://m.media-amazon.com/images/I/61P+Q+R+S+L.jpg', 0, 1500000),
(@GenshinBoxID, N'Nahida (Kusanali)', 'A', 0.045, 15, 'https://m.media-amazon.com/images/I/61A+B+C+D+EL.jpg', 0, 1500000);

-- B RANK (5 Thẻ - 5 Sao Popular)
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES 
(@GenshinBoxID, N'Hu Tao', 'B', 0.06, 40, 'https://m.media-amazon.com/images/I/51+1+2+3+FL.jpg', 0, 800000),
(@GenshinBoxID, N'Kamisato Ayaka', 'B', 0.06, 40, 'https://m.media-amazon.com/images/I/51+1+2+3+GL.jpg', 0, 800000),
(@GenshinBoxID, N'Ganyu', 'B', 0.06, 40, 'https://m.media-amazon.com/images/I/51+1+2+3+HL.jpg', 0, 800000),
(@GenshinBoxID, N'Xiao', 'B', 0.06, 40, 'https://m.media-amazon.com/images/I/51+1+2+3+IL.jpg', 0, 800000),
(@GenshinBoxID, N'Kaedehara Kazuha', 'B', 0.06, 40, 'https://m.media-amazon.com/images/I/51+1+2+3+JL.jpg', 0, 800000);

-- C RANK (7 Thẻ - 5 Sao Standard)
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES 
(@GenshinBoxID, N'Diluc', 'C', 0.042, 80, 'https://m.media-amazon.com/images/I/41+1+2+3+KL.jpg', 0, 300000),
(@GenshinBoxID, N'Keqing', 'C', 0.042, 80, 'https://m.media-amazon.com/images/I/41+1+2+3+LL.jpg', 0, 300000),
(@GenshinBoxID, N'Mona', 'C', 0.042, 80, 'https://m.media-amazon.com/images/I/41+1+2+3+ML.jpg', 0, 300000),
(@GenshinBoxID, N'Qiqi', 'C', 0.042, 80, 'https://m.media-amazon.com/images/I/41+1+2+3+NL.jpg', 0, 300000),
(@GenshinBoxID, N'Jean', 'C', 0.042, 80, 'https://m.media-amazon.com/images/I/41+1+2+3+OL.jpg', 0, 300000),
(@GenshinBoxID, N'Tighnari', 'C', 0.042, 80, 'https://m.media-amazon.com/images/I/41+1+2+3+PL.jpg', 0, 300000),
(@GenshinBoxID, N'Dehya', 'C', 0.042, 80, 'https://m.media-amazon.com/images/I/41+1+2+3+QL.jpg', 0, 300000);

-- D RANK (15 Thẻ - 4 Sao)
INSERT INTO BoxItems (BoxID, ItemName, RarityLevel, Probability, StockQuantity, ImageURL, IsHidden, MarketValue)
VALUES 
(@GenshinBoxID, N'Bennett', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+RL.jpg', 0, 50000),
(@GenshinBoxID, N'Xiangling', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+SL.jpg', 0, 50000),
(@GenshinBoxID, N'Xingqiu', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+TL.jpg', 0, 50000),
(@GenshinBoxID, N'Fischl', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+UL.jpg', 0, 50000),
(@GenshinBoxID, N'Barbara', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+VL.jpg', 0, 50000),
(@GenshinBoxID, N'Noelle', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+WL.jpg', 0, 50000),
(@GenshinBoxID, N'Sucrose', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+XL.jpg', 0, 50000),
(@GenshinBoxID, N'Razor', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+YL.jpg', 0, 50000),
(@GenshinBoxID, N'Lisa', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+ZL.jpg', 0, 50000),
(@GenshinBoxID, N'Kaeya', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+0L.jpg', 0, 50000),
(@GenshinBoxID, N'Amber', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+1L.jpg', 0, 50000),
(@GenshinBoxID, N'Chongyun', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+2L.jpg', 0, 50000),
(@GenshinBoxID, N'Beidou', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+3L.jpg', 0, 50000),
(@GenshinBoxID, N'Ningguang', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+4L.jpg', 0, 50000),
(@GenshinBoxID, N'Yanfei', 'D', 0.02, 300, 'https://m.media-amazon.com/images/I/31+1+2+3+5L.jpg', 0, 50000);

PRINT N'Đã thêm xong Box Genshin Impact!';

-- ==========================================
-- 1. CẬP NHẬT ONE PIECE (Local Images)
-- ==========================================
UPDATE BlindBoxes SET ImageURL = '/images/op-box.jpg' WHERE BoxName LIKE N'%One Piece%';

UPDATE BoxItems SET ImageURL = '/images/op-luffy-gear5.jpg' WHERE ItemName LIKE N'%Luffy Gear 5%';
UPDATE BoxItems SET ImageURL = '/images/op-law.webp' WHERE ItemName LIKE N'%Trafalgar Law%';
UPDATE BoxItems SET ImageURL = '/images/op-kid.webp' WHERE ItemName LIKE N'%Eustass%';
UPDATE BoxItems SET ImageURL = '/images/op-enel.webp' WHERE ItemName = N'Enel (God)';
UPDATE BoxItems SET ImageURL = '/images/op-sabo.jpg' WHERE ItemName LIKE N'%Sabo%';
UPDATE BoxItems SET ImageURL = '/images/op-yamato.jpg' WHERE ItemName = N'Yamato';
UPDATE BoxItems SET ImageURL = '/images/op-zoro.jpeg' WHERE ItemName = N'Roronoa Zoro';
UPDATE BoxItems SET ImageURL = '/images/op-kaido.jpg' WHERE ItemName LIKE N'%Kaido%';
UPDATE BoxItems SET ImageURL = '/images/op-nami.webp' WHERE ItemName = N'Nami';
UPDATE BoxItems SET ImageURL = '/images/op-sanji.jpg' WHERE ItemName = N'Sanji';
UPDATE BoxItems SET ImageURL = '/images/op-chopper.jpg' WHERE ItemName = N'Tony Tony Chopper';
UPDATE BoxItems SET ImageURL = '/images/op-robin.jpg' WHERE ItemName = N'Nico Robin';
UPDATE BoxItems SET ImageURL = '/images/op-franky.jpg' WHERE ItemName = N'Franky';
UPDATE BoxItems SET ImageURL = '/images/op-brook.png' WHERE ItemName = N'Brook';
UPDATE BoxItems SET ImageURL = '/images/op-jinbe.jpg' WHERE ItemName = N'Jinbe';
UPDATE BoxItems SET ImageURL = '/images/op-marine.jpg' WHERE ItemName = N'Marine Soldier';
UPDATE BoxItems SET ImageURL = '/images/op-crew.jpg' WHERE ItemName = N'Pirate Crew';
UPDATE BoxItems SET ImageURL = '/images/op-buggy.jpg' WHERE ItemName = N'Buggy';
UPDATE BoxItems SET ImageURL = '/images/op-alvida.jpg' WHERE ItemName = N'Alvida';
UPDATE BoxItems SET ImageURL = '/images/op-koby.webp' WHERE ItemName = N'Koby';
UPDATE BoxItems SET ImageURL = '/images/op-helmeppo.jpg' WHERE ItemName = N'Helmeppo';
UPDATE BoxItems SET ImageURL = '/images/op-morgan.jpg' WHERE ItemName = N'Morgan';
UPDATE BoxItems SET ImageURL = '/images/op-mohji.jpg' WHERE ItemName = N'Mohji';
UPDATE BoxItems SET ImageURL = '/images/op-cabaji.webp' WHERE ItemName = N'Cabaji';
UPDATE BoxItems SET ImageURL = '/images/op-gaimon.jpg' WHERE ItemName = N'Gaimon';
UPDATE BoxItems SET ImageURL = '/images/op-kuro.jpg' WHERE ItemName = N'Kuro';
UPDATE BoxItems SET ImageURL = '/images/op-jango.jpg' WHERE ItemName = N'Jango';
UPDATE BoxItems SET ImageURL = '/images/op-sham.jpg' WHERE ItemName = N'Sham';
UPDATE BoxItems SET ImageURL = '/images/op-buchi.jpg' WHERE ItemName = N'Buchi';
UPDATE BoxItems SET ImageURL = '/images/op-donkrieg.jpg' WHERE ItemName = N'Don Krieg';

-- ==========================================
-- 2. CẬP NHẬT GENSHIN IMPACT (Local Images)
-- ==========================================
UPDATE BlindBoxes SET ImageURL = '/images/gs-box.jpg' WHERE BoxName LIKE N'%Genshin Impact%';

UPDATE BoxItems SET ImageURL = '/images/gs-raiden.webp' WHERE ItemName LIKE N'%Raiden%';
UPDATE BoxItems SET ImageURL = '/images/gs-zhongli.webp' WHERE ItemName LIKE N'%Zhongli%';
UPDATE BoxItems SET ImageURL = '/images/gs-nahida.webp' WHERE ItemName LIKE N'%Nahida%';
UPDATE BoxItems SET ImageURL = '/images/gs-hutao.webp' WHERE ItemName = N'Hu Tao';
UPDATE BoxItems SET ImageURL = '/images/gs-ayaka.webp' WHERE ItemName = N'Kamisato Ayaka';
UPDATE BoxItems SET ImageURL = '/images/gs-ganyu.webp' WHERE ItemName = N'Ganyu';
UPDATE BoxItems SET ImageURL = '/images/gs-xiao.jpg' WHERE ItemName = N'Xiao';
UPDATE BoxItems SET ImageURL = '/images/gs-kazuha.webp' WHERE ItemName = N'Kaedehara Kazuha';
UPDATE BoxItems SET ImageURL = '/images/gs-diluc.jpg' WHERE ItemName = N'Diluc';
UPDATE BoxItems SET ImageURL = '/images/gs-keqing.jpg' WHERE ItemName = N'Keqing';
UPDATE BoxItems SET ImageURL = '/images/gs-mona.webp' WHERE ItemName = N'Mona';
UPDATE BoxItems SET ImageURL = '/images/gs-qiqi.webp' WHERE ItemName = N'Qiqi';
UPDATE BoxItems SET ImageURL = '/images/gs-jean.jpg' WHERE ItemName = N'Jean';
UPDATE BoxItems SET ImageURL = '/images/gs-tighnari.jpg' WHERE ItemName = N'Tighnari';
UPDATE BoxItems SET ImageURL = '/images/gs-dehya.jpg' WHERE ItemName = N'Dehya';
UPDATE BoxItems SET ImageURL = '/images/gs-bennett.jpg' WHERE ItemName = N'Bennett';
UPDATE BoxItems SET ImageURL = '/images/gs-xiangling.jpg' WHERE ItemName = N'Xiangling';
UPDATE BoxItems SET ImageURL = '/images/gs-xingqiu.webp' WHERE ItemName = N'Xingqiu';
UPDATE BoxItems SET ImageURL = '/images/gs-fischl.jpg' WHERE ItemName = N'Fischl';
UPDATE BoxItems SET ImageURL = '/images/gs-barbara.webp' WHERE ItemName = N'Barbara';
UPDATE BoxItems SET ImageURL = '/images/gs-noelle.webp' WHERE ItemName = N'Noelle';
UPDATE BoxItems SET ImageURL = '/images/gs-sucrose.jpg' WHERE ItemName = N'Sucrose';
UPDATE BoxItems SET ImageURL = '/images/gs-razor.jpg' WHERE ItemName = N'Razor';
UPDATE BoxItems SET ImageURL = '/images/gs-lisa.jpg' WHERE ItemName = N'Lisa';
UPDATE BoxItems SET ImageURL = '/images/gs-kaeya.jpg' WHERE ItemName = N'Kaeya';
UPDATE BoxItems SET ImageURL = '/images/gs-amber.webp' WHERE ItemName = N'Amber';
UPDATE BoxItems SET ImageURL = '/images/gs-chongyun.webp' WHERE ItemName = N'Chongyun';
UPDATE BoxItems SET ImageURL = '/images/gs-beidou.jpg' WHERE ItemName = N'Beidou';
UPDATE BoxItems SET ImageURL = '/images/gs-ningguang.jpg' WHERE ItemName = N'Ningguang';
UPDATE BoxItems SET ImageURL = '/images/gs-yanfei.jpg' WHERE ItemName = N'Yanfei';

PRINT N'Đã cập nhật xong toàn bộ ảnh từng nhân vật!';

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