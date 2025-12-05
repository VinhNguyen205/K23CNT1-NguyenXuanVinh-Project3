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