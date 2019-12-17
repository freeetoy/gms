# gms

//2019-12-17
adminlte.css 파일

.table th,
.table td {
  padding: 0.75rem; => 50rem 으로 수정
  vertical-align: top;
  border-top: 1px solid #dee2e6;
}

DB


DROP TABLE TB_Customer_Product_Price;

DROP TABLE TB_USER;

DROP TABLE TB_Code;

DROP TABLE TB_Bottle_Hist;

DROP TABLE TB_Order_Product;

DROP TABLE TB_Product_Price;

DROP TABLE TB_Product;

DROP TABLE TB_Gas;

DROP TABLE TB_Bottle;

DROP TABLE TB_Bottle_Transaction;

DROP TABLE TB_Order;

DROP TABLE TB_Customer;

DROP TABLE TB_Member_Company;

DROP TABLE TB_Daily_Stat_Product;

DROP TABLE TB_Monthly_Stat_Product;

DROP TABLE TB_Daily_Stat_Customer;

DROP TABLE TB_Monthly_Stat_Customer;

DROP TABLE TB_Daily_Stat_Bottle;

DROP TABLE TB_Monthly_Stat_Bottle;

DROP TABLE TB_Daily_Stat_Order;

DROP TABLE TB_Monthly_Stat_Order;

DROP TABLE TB_Schedule;


CREATE TABLE TB_Calendar(
	CAL_SEQ			NUMBER(8) NOT NULL AUTO_INCREMENT,
	CAL_ID			VARCHAR(50) NOT NULL,
	CAL_TITLE		VARCHAR(200) NOT NULL,
	CAL_CONTENT		VARCHAR(4000) NOT NULL,
	CAL_DATE		VARCHAR(12) NOT NULL,
	Create_ID		VARCHAR(20) NULL,
	Create_Dt		DATETIME NULL,
	Update_ID		VARCHAR(20) NULL,
	Update_Dt		DATETIME NULL,	
	PRIMARY KEY (SEQ)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;





CREATE TABLE TB_Bottle
(
	Bottle_ID            CHAR(10) NOT NULL,
	Member_Comp_Seq      INTEGER NOT NULL,
	Order_ID             INTEGER NULL,
	Order_Product_Seq    INTEGER NULL,
	Bottle_BarCd         CHAR(10) NULL,	
	Gas_ID               INTEGER NOT NULL,
	Bottle_Capa          VARCHAR(10) NULL,
	Bottle_Create_Dt     DATETIME NULL,
	Bottle_Charge_Dt     DATETIME NULL,	
	Bottle_Volumn        VARCHAR(5) NULL,
	Bottle_Charge_Prss   VARCHAR(3) NULL,
	Bottle_Sales_YN      CHAR(1) NULL,
	Bottle_Work_CD       CHAR(4) NULL,	
	Bottle_Work_ID       VARCHAR(20) NULL,
	Customer_ID          INTEGER NULL,
	Bottle_Type	     CHAR(1) DEFAULT 'E',
	Delete_YN            CHAR(1) DEFAULT 'N',
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,	
	PRIMARY KEY (Bottle_ID)
);



CREATE UNIQUE INDEX IDX_BOTTLE_PK ON TB_Bottle
(
	Bottle_ID
);



CREATE UNIQUE INDEX IDX_Bottle_Member_Comp ON TB_Bottle
(
	Member_Comp_Seq,
	Bottle_ID,
	Create_Dt
);



CREATE TABLE TB_Bottle_Hist
(
	Bottle_Hist_Seq      INTEGER NOT NULL,
	Bottle_ID            CHAR(10) NOT NULL,
	Member_Comp_Seq      INTEGER NOT NULL,
	Order_ID             INTEGER NULL,
	Order_Product_Seq    INTEGER NULL,
	Bottle_BarCd         CHAR(10) NULL,	
	Gas_ID               INTEGER NOT NULL,
	Bottle_Capa          VARCHAR(10) NULL,
	Bottle_Create_Dt     DATETIME NULL,
	Bottle_Charge_Dt     DATETIME NULL,	
	Bottle_Volumn        VARCHAR(5) NULL,
	Bottle_Charge_Prss   VARCHAR(3) NULL,
	Bottle_Sales_YN      CHAR(1) NULL,
	Bottle_Work_CD       CHAR(4) NULL,	
	Bottle_Work_ID       VARCHAR(20) NULL,
	Customer_ID          INTEGER NULL,
	Bottle_Type	     CHAR(1) NULL,
	Delete_YN            CHAR(1) NULL,
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,	
	PRIMARY KEY (Bottle_Hist_Seq,Bottle_ID)
);



CREATE UNIQUE INDEX IDX_BOTTLE_HIST_PK ON TB_Bottle_Hist
(
	Bottle_Hist_Seq,
	Bottle_ID
);



CREATE UNIQUE INDEX IDX_Bottle_Member_Comp ON TB_Bottle_Hist
(
	Member_Comp_Seq,
	Create_Dt
);



CREATE UNIQUE INDEX IDX_BOTTLE_HIST_PK ON TB_Bottle_Hist
(
	Bottle_ID,
	Bottle_Hist_Seq
);



CREATE UNIQUE INDEX IDX_Bottle_Member_Comp ON TB_Bottle_Hist
(
	Member_Comp_Seq,
	Create_Dt
);




CREATE TABLE TB_Code
(
	CD_ID                CHAR(4) NOT NULL,
	CD_NM                VARCHAR(20) NULL,
	CD_GRP_ID            CHAR(4) NULL,
	Delete_YN            CHAR(1) NULL,
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,
	PRIMARY KEY (CD_ID)
);



CREATE UNIQUE INDEX IDX_Code_PK ON TB_Code
(
	CD_ID
);



CREATE TABLE TB_Customer
(
	Customer_ID          INTEGER NOT NULL AUTO_INCREMENT,
	Customer_Nm          VARCHAR(100) NULL,
	Business_Reg_ID      VARCHAR(15) NULL,
	Customer_Rep_Nm      VARCHAR(15) NULL,
	Customer_Busi_Type   VARCHAR(15) NULL,
	Customer_Item        VARCHAR(30) NULL,
	Customer_Addr        VARCHAR(100) NULL,
	Customer_Phone       VARCHAR(20) NULL,
	Customer_Email       VARCHAR(50) NULL,
	Customer_Status      CHAR(1) NULL,
	Member_Comp_Seq      INTEGER NOT NULL,
	Sales_ID             VARCHAR(20) NULL,
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,
	PRIMARY KEY (Customer_ID)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;



CREATE UNIQUE INDEX IDX_CUSTOMER_PK ON TB_Customer
(
	Customer_ID
);



CREATE UNIQUE INDEX IDX_Customer_Member_Comp ON TB_Customer
(
	Member_Comp_Seq,
	Customer_ID
);



CREATE TABLE TB_Customer_Product_Price
(
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,
	Product_Price        NUMERIC(7) NULL,
	Customer_ID          INTEGER NOT NULL,
	Product_ID           INTEGER NOT NULL,
	Product_Price_Seq    INTEGER NOT NULL,
	PRIMARY KEY (Customer_ID,Product_ID,Product_Price_Seq)
);



CREATE UNIQUE INDEX IDX_CUSTOMER_PRODUCT_PRICE_PK ON TB_Customer_Product_Price
(
	Customer_ID,
	Product_ID,
	Product_Price_Seq
);



CREATE TABLE TB_Gas
(
	Gas_ID               INTEGER NOT NULL AUTO_INCREMENT,
	Gas_Nm               VARCHAR(20) NULL,
	Gas_CD               VARCHAR(10) NULL,	
	Member_Comp_Seq      INTEGER NOT NULL,
	Delete_YN            CHAR(1) NULL,
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,
	PRIMARY KEY (Gas_ID)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;



CREATE UNIQUE INDEX IDX_GAS_PK ON TB_Gas
(
	Gas_ID
);



CREATE UNIQUE INDEX IDX_Gas_Member_Comp ON TB_Gas
(
	Member_Comp_Seq,
	Gas_ID
);



CREATE TABLE TB_Member_Company
(
	Member_Comp_Seq      INTEGER NOT NULL AUTO_INCREMENT,
	Member_Comp_Nm       VARCHAR(100) NULL,
	Manager_Nm           VARCHAR(20) NULL,
	Manager_Phone        VARCHAR(20) NULL,
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,
	PRIMARY KEY (Member_Comp_Seq)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;



CREATE UNIQUE INDEX IDX_Member_Company_PK ON TB_Member_Company
(
	Member_Comp_Seq
);



CREATE TABLE TB_Order
(
	Order_ID             INTEGER NOT NULL,
	Member_Comp_Seq      INTEGER NOT NULL,
	Customer_ID          INTEGER NOT NULL,
	Order_Type_CD        CHAR(4) NULL,
	Delivery_Req_Dt      DATETIME NULL,
	Delivery_Req_AmPm    CHAR(1) NULL,
	Order_Etc            VARCHAR(100) NULL,	
	Order_Product_Nm     VARCHAR(20) NULL,
	Order_Product_Capa   VARCHAR(10) NULL,	
	Order_Process_CD     CHAR(4) NULL,	
	Sales_ID             VARCHAR(20) NULL,
	Order_Delivery_Dt    DATETIME NULL,	
	Order_Total_Amount   NUMERIC(10) NULL,
	Deposit_Check_Dt     DATE NULL,
	Deposit_Amount       NUMERIC(10) NULL,
	Deposit_Bank_Cd      CHAR(4) NULL,	
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,
	PRIMARY KEY (Order_ID)
);



CREATE UNIQUE INDEX IDX_ORDER_PK ON TB_Order
(
	Order_ID
);



CREATE UNIQUE INDEX IDX_Order_Member_Comp ON TB_Order
(
	Member_Comp_Seq,
	Order_ID
);



CREATE TABLE TB_Order_Product
(
	Order_ID             INTEGER NOT NULL,
	Order_Product_Seq    INTEGER NOT NULL,
	Product_ID           INTEGER NOT NULL,
	Product_Price_Seq    INTEGER NOT NULL,
	Order_Count          NUMERIC(2) NULL,
	Bottle_Change_YN     CHAR(1) NULL,
	Order_Product_ETC    VARCHAR(100) NULL,
	Order_Amount         NUMERIC(10) NULL,
	Bottle_ID            CHAR(10) NULL,	
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,
	PRIMARY KEY (Order_ID,Order_Product_Seq)
);



CREATE UNIQUE INDEX IDX_ORDER_PRODUCT_PK ON TB_Order_Product
(
	Order_ID,
	Order_Product_Seq
);





CREATE TABLE TB_Product
(
	Product_ID           INTEGER NOT NULL,
	Product_Nm           VARCHAR(20) NULL,
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,
	Gas_ID               INTEGER NULL,
	Product_Status       CHAR(1) NULL,
	Member_Comp_Seq      INTEGER NOT NULL,
	PRIMARY KEY (Product_ID)
);



CREATE UNIQUE INDEX IDX_PRODUCT_PK ON TB_Product
(
	Product_ID
);



CREATE UNIQUE INDEX IDX_Prodcut_Member_Comp ON TB_Product
(
	Member_Comp_Seq,
	Product_ID
);



CREATE TABLE TB_Product_Price
(
	Product_ID           INTEGER NOT NULL,
	Product_Price_Seq    INTEGER NOT NULL,
	Product_Price        NUMERIC(7) NULL,
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,
	Product_Capa         VARCHAR(10) NULL,
	Product_Status       CHAR(1) NULL DEFAULT 'N',
	PRIMARY KEY (Product_ID,Product_Price_Seq)
);



CREATE UNIQUE INDEX IDX_ProductPrice_PK ON TB_Product_Price
(
	Product_ID,
	Product_Price_Seq
);



CREATE UNIQUE INDEX IDX_ProductPrice_PK ON TB_Product_Price
(
	Product_ID,
	Product_Price_Seq
);


CREATE TABLE TB_USER
(
	User_ID              VARCHAR(20) NOT NULL,
	User_Nm              VARCHAR(20) NULL,
	User_Phone           VARCHAR(20) NULL,
	User_Part_CD         CHAR(4) NULL,
	User_Authority       CHAR(2) NULL,
	User_Passwd          VARCHAR(255) NULL,
	Create_Dt            DATETIME NULL,
	Update_Dt            DATETIME NULL,
	Create_ID            VARCHAR(20) NULL,
	Update_ID            VARCHAR(20) NULL,
	Last_Connect_Dt      DATE NULL,
	Member_Comp_Seq      INTEGER NOT NULL,
	Delete_YN            CHAR(1) NULL,
	PRIMARY KEY (User_ID)
);



CREATE UNIQUE INDEX IDX_USER_PK ON TB_USER
(
	User_ID
);



CREATE TABLE TB_Daily_Stat_Bottle
(
	Stat_Dt              CHAR(10) NOT NULL,
	Bottle_Input_Count   NUMERIC(5) NULL,
	Bottle_Charge_Count  NUMERIC(5) NULL,
	Bottle_Sales_Count   NUMERIC(5) NULL,
	Bottle_Back_Count    NUMERIC(5) NULL,
	Bottle_Discard_Count NUMERIC(5) NULL,
	Create_Id            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,	
	PRIMARY KEY (Stat_Dt)
);



CREATE UNIQUE INDEX ID_Daily_Bottle_PK ON TB_Daily_Stat_Bottle
(
	Stat_Dt
);




CREATE TABLE TB_Daily_Stat_Customer
(
	Stat_Dt              CHAR(10) NOT NULL,
	Customer_ID          INTEGER NOT NULL,
	Order_Count          INTEGER NULL,
	Order_Amount         NUMERIC(10) NULL,
	Create_Dt            DATETIME NULL,
	Create_ID            VARCHAR(20) NULL,
	
	PRIMARY KEY (Customer_ID,Stat_Dt)
);



CREATE UNIQUE INDEX ID_Daily_Customer_PK ON TB_Daily_Stat_Customer
(
	Customer_ID,
	Stat_Dt
);



CREATE TABLE TB_Daily_Stat_Product
(
	Stat_Dt              CHAR(10) NOT NULL,
	Product_ID           INTEGER NOT NULL,
	Order_Count          INTEGER NULL,
	Order_Amount         NUMERIC(10) NULL,
	Create_Dt            DATETIME NULL,
	Create_ID            VARCHAR(20) NULL,	
	PRIMARY KEY (Product_ID,Stat_Dt)
);



CREATE UNIQUE INDEX ID_Daily_Product_PK ON TB_Daily_Stat_Product
(
	Product_ID,
	Stat_Dt
);

CREATE TABLE TB_Daily_Stat_Order
(
	Stat_Dt              CHAR(10) NOT NULL,
	Order_Total_Count    NUMERIC(2) NULL,
	Order_Total_Amount   NUMERIC(10) NULL,
	Order_Product_Count  NUMERIC(2) NULL,
	Order_Auto_Count     NUMERIC(2) NULL,
	Order_Cancel_Count   NUMERIC(2) NULL,
	Order_Retrieved_Count NUMERIC(2) NULL,
	Order_Check_Count    NUMERIC(2) NULL,
	Order_Etc_Count      NUMERIC(2) NULL,
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,	
	PRIMARY KEY (Stat_Dt)
);



CREATE UNIQUE INDEX ID_Daily_Order_PK ON TB_Daily_Stat_Order
(
	Stat_Dt
);



CREATE TABLE TB_Monthly_Stat_Bottle
(
	Stat_Dt              CHAR(7) NOT NULL,
	Bottle_Input_Count   NUMERIC(5) NULL,
	Bottle_Charge_Count  NUMERIC(5) NULL,
	Bottle_Sales_Count   NUMERIC(5) NULL,
	Bottle_Back_Count    NUMERIC(5) NULL,
	Bottle_Discard_Count NUMERIC(5) NULL,
	Create_Id            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,	
	PRIMARY KEY (Stat_Dt)
);



CREATE UNIQUE INDEX ID_Monthly_Bottle_PK ON TB_Monthly_Stat_Bottle
(
	Stat_Dt
);



CREATE TABLE TB_Monthly_Stat_Customer
(
	Stat_Dt              CHAR(7) NOT NULL,
	Customer_ID          INTEGER NOT NULL,
	Order_Count          INTEGER NULL,
	Order_Amount         NUMERIC(10) NULL,
	Create_Dt            DATETIME NULL,
	Create_ID            VARCHAR(20) NULL,	
	PRIMARY KEY (Customer_ID,Stat_Dt)
);



CREATE UNIQUE INDEX ID_Monthly_Customer_PK ON TB_Monthly_Stat_Customer
(
	Customer_ID,
	Stat_Dt
);



CREATE TABLE TB_Monthly_Stat_Product
(
	Stat_Dt              CHAR(7) NOT NULL,
	Product_ID           INTEGER NOT NULL,	
	Order_Count          INTEGER NULL,
	Order_Amount         NUMERIC(10) NULL,
	Create_Dt            DATETIME NULL,
	Create_ID            VARCHAR(20) NULL,
	PRIMARY KEY (Product_ID,Stat_Dt)
);



CREATE UNIQUE INDEX ID_Monthly_Product_PK ON TB_Monthly_Stat_Product
(
	Product_ID,
	Stat_Dt
);


CREATE TABLE TB_Monthly_Stat_Order
(
	Stat_Dt              CHAR(7) NOT NULL,
	Order_Total_Count    NUMERIC(2) NULL,
	Order_Total_Amount   NUMERIC(10) NULL,
	Order_Product_Count  NUMERIC(2) NULL,
	Order_Auto_Count     NUMERIC(2) NULL,
	Order_Cancel_Count   NUMERIC(2) NULL,
	Order_Retrieved_Count NUMERIC(2) NULL,
	Order_Check_Count    NUMERIC(2) NULL,
	Order_Etc_Count      NUMERIC(2) NULL,
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,	
	PRIMARY KEY (Stat_Dt)
);



CREATE UNIQUE INDEX ID_Monthly_Order_PK ON TB_Monthly_Stat_Order
(
	Stat_Dt
);

DROP TABLE TB_Work_Bottle;

CREATE TABLE TB_Work_Bottle
(
	Work_Report_Seq      INTEGER NOT NULL,
	Bottle_ID            CHAR(10) NOT NULL,	
	Bottle_BarCD        CHAR(10) NULL,
	Customer_ID          INTEGER NULL,
	Bottle_Work_Cd       CHAR(4) NULL,
	Gas_ID               INTEGER NULL,
	Product_ID           INTEGER NULL,
	Product_Price_Seq    INTEGER NULL,	
	Bottle_Type          CHAR(1) NULL,
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,	
	PRIMARY KEY (Work_Report_Seq,Bottle_ID)
);



CREATE UNIQUE INDEX IDX_Work_Bottle_PK ON TB_Work_Bottle
(
	Work_Report_Seq,
	Bottle_ID
);


DROP TABLE TB_Work_Report;

CREATE TABLE TB_Work_Report
(
	Work_Report_Seq      INTEGER NOT NULL AUTO_INCREMENT,
	User_ID              VARCHAR(20) NOT NULL ,
	Customer_ID          INTEGER NOT NULL,	
	Order_ID             INTEGER NOT NULL,
	Work_Dt              DATETIME NULL,
	Order_Amount         NUMERIC(10) NULL,
	Received_Amount      NUMERIC(10) NULL,
	Bottle_Type          CHAR(1) NULL,
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,	
	PRIMARY KEY (Work_Report_Seq)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;




CREATE UNIQUE INDEX IDX_Work_Report_PK ON TB_Work_Report
(
	Work_Report_Seq
);




CREATE TABLE TB_Schedule
(
	Schedule_Seq         INTEGER NOT NULL AUTO_INCREMENT,
	User_ID              VARCHAR(20) NOT NULL,
	Schedule_Title       VARCHAR(30) NULL,
	Schedule_Type        CHAR(1) NULL,
	Schedule_Start_Dt    DATETIME NULL,
	Schedule_End_Dt      DATETIME NULL,
	Vacation_Gubun       CHAR(1) NULL,
	Delete_YN            CHAR(1) NULL,
	Create_ID            VARCHAR(20) NULL,
	Create_Dt            DATETIME NULL,
	Update_ID            VARCHAR(20) NULL,
	Update_Dt            DATETIME NULL,
	PRIMARY KEY (Schedule_Seq)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;



CREATE UNIQUE INDEX IDX_Schedule_PK ON TB_Schedule
(
	Schedule_Seq
);


ALTER TABLE TB_Bottle COMMENT = '용기';

 ALTER TABLE TB_Bottle CHANGE COLUMN Bottle_ID Bottle_ID CHAR(10) NOT NULL COMMENT '용기ID';
 ALTER TABLE TB_Bottle CHANGE COLUMN Order_ID Order_ID INTEGER NULL COMMENT '주문ID';
 ALTER TABLE TB_Bottle CHANGE COLUMN Order_Product_Seq Order_Product_Seq INTEGER NULL COMMENT '주문상품순번';
 ALTER TABLE TB_Bottle CHANGE COLUMN Member_Comp_Seq Member_Comp_Seq INTEGER NOT NULL COMMENT '회원사순번';
 ALTER TABLE TB_Bottle CHANGE COLUMN Bottle_BarCd Bottle_BarCd CHAR(10) NULL COMMENT '용기바코드';
 ALTER TABLE TB_Bottle CHANGE COLUMN Gas_ID Gas_ID INTEGER NOT NULL COMMENT '가스ID';
 ALTER TABLE TB_Bottle CHANGE COLUMN Bottle_Capa Bottle_Capa VARCHAR(10) NULL COMMENT '용기용량';
 ALTER TABLE TB_Bottle CHANGE COLUMN Bottle_Create_Dt Bottle_Create_Dt DATETIME NULL COMMENT '용기제조일자';
 ALTER TABLE TB_Bottle CHANGE COLUMN Bottle_Charge_Dt Bottle_Charge_Dt DATETIME NULL COMMENT '용기충전기한일자';
 ALTER TABLE TB_Bottle CHANGE COLUMN Bottle_Volumn Bottle_Volumn VARCHAR(5) NULL COMMENT '용기체적';
 ALTER TABLE TB_Bottle CHANGE COLUMN Bottle_Charge_Prss Bottle_Charge_Prss VARCHAR(3) NULL COMMENT '용기충전압력';
 ALTER TABLE TB_Bottle CHANGE COLUMN Bottle_Sales_YN Bottle_Sales_YN CHAR(1) NULL COMMENT '용기판매여부';
 ALTER TABLE TB_Bottle CHANGE COLUMN Bottle_Work_CD Bottle_Work_CD CHAR(4) NULL COMMENT '용기작업코드';
 ALTER TABLE TB_Bottle CHANGE COLUMN Bottle_Work_ID Bottle_Work_ID VARCHAR(20) NULL COMMENT '용기작업자ID';
 ALTER TABLE TB_Bottle CHANGE COLUMN Customer_ID Customer_ID INTEGER NULL COMMENT '거래처ID';
 ALTER TABLE TB_Bottle CHANGE COLUMN Bottle_Type Bottle_Type CHAR(1) NULL COMMENT '용기타입';
 ALTER TABLE TB_Bottle CHANGE COLUMN Delete_YN Delete_YN CHAR(1) NULL COMMENT '삭제여부';
 ALTER TABLE TB_Bottle CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Bottle CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Bottle CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Bottle CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 

ALTER TABLE TB_Bottle_Hist COMMENT = '용기이력';

 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Bottle_Hist_Seq Bottle_Hist_Seq INTEGER NOT NULL COMMENT '용기이력순번';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Bottle_ID Bottle_ID CHAR(10) NOT NULL COMMENT '용기ID';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Order_ID Order_ID INTEGER NULL COMMENT '주문ID';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Order_Product_Seq Order_Product_Seq INTEGER NULL COMMENT '주문상품순번';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Member_Comp_Seq Member_Comp_Seq INTEGER NULL COMMENT '회원사순번';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Bottle_BarCd Bottle_BarCd CHAR(10) NULL COMMENT '용기바코드';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN GAS_ID GAS_ID VARCHAR(10) NULL COMMENT '가스ID';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Bottle_Charge_Dt Bottle_Charge_Dt DATETIME NULL COMMENT '용기충전기한일자';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Bottle_Capa Bottle_Capa VARCHAR(10) NULL COMMENT '용기용량';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Bottle_Volumn Bottle_Volumn VARCHAR(5) NULL COMMENT '용기체적';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Bottle_Charge_Prss Bottle_Charge_Prss VARCHAR(3) NULL COMMENT '용기충전압력';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Bottle_Sales_YN Bottle_Sales_YN CHAR(1) NULL COMMENT '용기판매여부';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Bottle_Work_CD Bottle_Work_CD CHAR(4) NULL COMMENT '용기작업코드';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Bottle_Work_ID Bottle_Work_ID VARCHAR(20) NULL COMMENT '용기작업자ID';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Bottle_Create_Dt Bottle_Create_Dt DATETIME NULL COMMENT '용기제조일자';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Customer_ID Customer_ID INTEGER NULL COMMENT '거래처ID';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Bottle_Type Bottle_Type CHAR(1) NULL COMMENT '용기타입';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Delet_YN Delet_YN CHAR(1) NULL COMMENT '삭제여부';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Bottle_Hist CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 

ALTER TABLE TB_Code COMMENT = '코드';

 ALTER TABLE TB_Code CHANGE COLUMN CD_ID CD_ID CHAR(4) NOT NULL COMMENT '코드ID';
 ALTER TABLE TB_Code CHANGE COLUMN CD_NM CD_NM VARCHAR(20) NULL COMMENT '코드명';
 ALTER TABLE TB_Code CHANGE COLUMN CD_GRP_ID CD_GRP_ID CHAR(4) NULL COMMENT '코드그룹ID';
 ALTER TABLE TB_Code CHANGE COLUMN Delete_YN Delete_YN CHAR(1) NULL COMMENT '삭제여부';
 ALTER TABLE TB_Code CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Code CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Code CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Code CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 

ALTER TABLE TB_Customer COMMENT = '거래처';

 ALTER TABLE TB_Customer CHANGE COLUMN Customer_ID Customer_ID INTEGER NOT NULL COMMENT '거래처ID';
 ALTER TABLE TB_Customer CHANGE COLUMN Member_Comp_Seq Member_Comp_Seq INTEGER NOT NULL COMMENT '회원사순번';
 ALTER TABLE TB_Customer CHANGE COLUMN Customer_Nm Customer_Nm VARCHAR(100) NULL COMMENT '거래처명';
 ALTER TABLE TB_Customer CHANGE COLUMN Business_Reg_ID Business_Reg_ID CHAR(12) NULL COMMENT '사업자등록번호';
 ALTER TABLE TB_Customer CHANGE COLUMN Customer_Rep_Nm Customer_Rep_Nm VARCHAR(15) NULL COMMENT '거래처대표자명';
 ALTER TABLE TB_Customer CHANGE COLUMN Customer_Busi_Type Customer_Busi_Type VARCHAR(15) NULL COMMENT '거래처업태';
 ALTER TABLE TB_Customer CHANGE COLUMN Customer_Item Customer_Item VARCHAR(30) NULL COMMENT '거래처종목';
 ALTER TABLE TB_Customer CHANGE COLUMN Customer_Addr Customer_Addr VARCHAR(100) NULL COMMENT '거래처주소';
 ALTER TABLE TB_Customer CHANGE COLUMN Customer_Phone Customer_Phone VARCHAR(20) NULL COMMENT '거래처전화번호';
 ALTER TABLE TB_Customer CHANGE COLUMN Customer_Email Customer_Email VARCHAR(50) NULL COMMENT '거래처이메일';
 ALTER TABLE TB_Product CHANGE COLUMN Customer_Status Customer_Status CHAR(1) NULL COMMENT '거래처상태';
 ALTER TABLE TB_Customer CHANGE COLUMN Sales_ID Sales_ID VARCHAR(20) NULL COMMENT '영업담당자';
 ALTER TABLE TB_Customer CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Customer CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Customer CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Customer CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 

ALTER TABLE TB_Customer_Product_Price COMMENT = '거래처상품가격';

 ALTER TABLE TB_Customer_Product_Price CHANGE COLUMN Customer_ID Customer_ID INTEGER NOT NULL COMMENT '거래처ID';
 ALTER TABLE TB_Customer_Product_Price CHANGE COLUMN Product_ID Product_ID INTEGER NOT NULL COMMENT '상품ID';
 ALTER TABLE TB_Customer_Product_Price CHANGE COLUMN Product_Price_Seq Product_Price_Seq INTEGER NOT NULL COMMENT '상품가격순번';
 ALTER TABLE TB_Customer_Product_Price CHANGE COLUMN Product_Price Product_Price NUMERIC(7) NULL COMMENT '상품가격';
 ALTER TABLE TB_Customer_Product_Price CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Customer_Product_Price CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Customer_Product_Price CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Customer_Product_Price CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 

ALTER TABLE TB_Gas COMMENT = '가스';

 ALTER TABLE TB_Gas CHANGE COLUMN Gas_ID Gas_ID INTEGER NOT NULL COMMENT '가스ID';
 ALTER TABLE TB_Gas CHANGE COLUMN Member_Comp_Seq Member_Comp_Seq INTEGER NOT NULL COMMENT '회원사순번';
 ALTER TABLE TB_Gas CHANGE COLUMN Gas_Nm Gas_Nm VARCHAR(20) NULL COMMENT '가스명';
 ALTER TABLE TB_Gas CHANGE COLUMN Gas_CD Gas_CD VARCHAR(10) NULL COMMENT '가스코드';
 ALTER TABLE TB_Gas CHANGE COLUMN Delete_YN Delete_YN CHAR(1) NULL COMMENT '삭제여부';
 ALTER TABLE TB_Gas CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Gas CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Gas CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Gas CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 

ALTER TABLE TB_Member_Company COMMENT = '회원사';

 ALTER TABLE TB_Member_Company CHANGE COLUMN Member_Comp_Seq Member_Comp_Seq INTEGER NOT NULL COMMENT '회원사순번';
 ALTER TABLE TB_Member_Company CHANGE COLUMN Member_Comp_Nm Member_Comp_Nm VARCHAR(100) NULL COMMENT '회원사명';
 ALTER TABLE TB_Member_Company CHANGE COLUMN Manager_Nm Manager_Nm VARCHAR(20) NULL COMMENT '담당자명';
 ALTER TABLE TB_Member_Company CHANGE COLUMN Manager_Phone Manager_Phone VARCHAR(20) NULL COMMENT '담당자연락처';
 ALTER TABLE TB_Member_Company CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Member_Company CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Member_Company CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Member_Company CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 

ALTER TABLE TB_Order COMMENT = '주문';

 ALTER TABLE TB_Order CHANGE COLUMN Order_ID Order_ID INTEGER NOT NULL COMMENT '주문ID';
 ALTER TABLE TB_Order CHANGE COLUMN Member_Comp_Seq Member_Comp_Seq INTEGER NOT NULL COMMENT '회원사순번';
 ALTER TABLE TB_Order CHANGE COLUMN Customer_ID Customer_ID INTEGER NOT NULL COMMENT '거래처ID';
 ALTER TABLE TB_Order CHANGE COLUMN Order_Type_CD Order_Type_CD CHAR(4) NULL COMMENT '주문타입';
 ALTER TABLE TB_Order CHANGE COLUMN Delivery_Req_Dt Delivery_Req_Dt DATETIME NULL COMMENT '납품요청일';
 ALTER TABLE TB_Order CHANGE COLUMN Delivery_Req_AmPm Delivery_Req_AmPm CHAR(1) NULL COMMENT '납품요청일오전오후';
 ALTER TABLE TB_Order CHANGE COLUMN Order_Etc Order_Etc VARCHAR(100) NULL COMMENT '주문기타사항';
 ALTER TABLE TB_Order CHANGE COLUMN Order_Product_Nm Order_Product_Nm VARCHAR(20) NULL COMMENT '주문상품';
 ALTER TABLE TB_Order CHANGE COLUMN Order_Product_Capa Order_Product_Capa VARCHAR(10) NULL COMMENT '주문상품용량';
 ALTER TABLE TB_Order CHANGE COLUMN Order_Total_Amount Order_Total_Amount NUMERIC(10) NULL COMMENT '주문금액';
 ALTER TABLE TB_Order CHANGE COLUMN Order_Delivery_Dt Order_Delivery_Dt DATETIME NULL COMMENT '주문납품일';
 ALTER TABLE TB_Order CHANGE COLUMN Sales_ID Sales_ID VARCHAR(20) NULL COMMENT '영업담당자';
 ALTER TABLE TB_Order CHANGE COLUMN Order_Process_CD Order_Process_CD CHAR(4) NULL COMMENT '주문진행코드';
 ALTER TABLE TB_Order CHANGE COLUMN Deposit_Check_Dt Deposit_Check_Dt DATE NULL COMMENT '입금확인일';
 ALTER TABLE TB_Order CHANGE COLUMN Deposit_Amount Deposit_Amount NUMERIC(10) NULL COMMENT '입금액';
 ALTER TABLE TB_Order CHANGE COLUMN Deposit_Bank_Cd Deposit_Bank_Cd CHAR(4) NULL COMMENT '입금은행';
 ALTER TABLE TB_Order CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Order CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Order CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Order CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 

ALTER TABLE TB_Order_Product COMMENT = '주문상품';

 ALTER TABLE TB_Order_Product CHANGE COLUMN Order_ID Order_ID INTEGER NOT NULL COMMENT '주문ID';
 ALTER TABLE TB_Order_Product CHANGE COLUMN Order_Product_Seq Order_Product_Seq INTEGER NOT NULL COMMENT '주문상품순번';
 ALTER TABLE TB_Order_Product CHANGE COLUMN Product_ID Product_ID INTEGER NOT NULL COMMENT '상품ID';
 ALTER TABLE TB_Order_Product CHANGE COLUMN Product_Price_Seq Product_Price_Seq INTEGER NOT NULL COMMENT '상품가격순번';
 ALTER TABLE TB_Order_Product CHANGE COLUMN Bottle_ID Bottle_ID CHAR(10) NULL COMMENT '용기ID';
 ALTER TABLE TB_Order_Product CHANGE COLUMN Order_Count Order_Count NUMERIC(2) NULL COMMENT '주문수량';
 ALTER TABLE TB_Order_Product CHANGE COLUMN Order_Product_ETC Order_Product_ETC VARCHAR(100) NULL COMMENT '주문상품기타';
 ALTER TABLE TB_Order_Product CHANGE COLUMN Bottle_Change_YN Bottle_Change_YN CHAR(1) NULL COMMENT '용기맞교환여부';
 ALTER TABLE TB_Order_Product CHANGE COLUMN Order_Amount Order_Amount NUMERIC(10) NULL COMMENT '주문가격';
 ALTER TABLE TB_Order_Product CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Order_Product CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Order_Product CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Order_Product CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 
 

ALTER TABLE TB_Product COMMENT = '상품';

 ALTER TABLE TB_Product CHANGE COLUMN Product_ID Product_ID INTEGER NOT NULL COMMENT '상품ID';
 ALTER TABLE TB_Product CHANGE COLUMN Member_Comp_Seq Member_Comp_Seq INTEGER NOT NULL COMMENT '회원사순번';
 ALTER TABLE TB_Product CHANGE COLUMN Gas_ID Gas_ID INTEGER NULL COMMENT '가스ID';
 ALTER TABLE TB_Product CHANGE COLUMN Product_Nm Product_Nm VARCHAR(20) NULL COMMENT '상품명';
 ALTER TABLE TB_Product CHANGE COLUMN Product_Status Product_Status CHAR(1) NULL COMMENT '상품상태';
 ALTER TABLE TB_Product CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Product CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Product CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Product CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 

ALTER TABLE TB_Product_Price COMMENT = '상품가격';

 ALTER TABLE TB_Product_Price CHANGE COLUMN Product_ID Product_ID INTEGER NOT NULL COMMENT '상품ID';
 ALTER TABLE TB_Product_Price CHANGE COLUMN Product_Price_Seq Product_Price_Seq INTEGER NOT NULL COMMENT '상품가격순번';
 ALTER TABLE TB_Product_Price CHANGE COLUMN Product_Price Product_Price NUMERIC(7) NULL COMMENT '상품가격';
 ALTER TABLE TB_Product_Price CHANGE COLUMN Product_Capa Product_Capa VARCHAR(10) NULL COMMENT '상품용량';
 ALTER TABLE TB_Product_Price CHANGE COLUMN Product_Status Product_Status CHAR(1) NULL COMMENT '상품상태';
 ALTER TABLE TB_Product_Price CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Product_Price CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Product_Price CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Product_Price CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';


ALTER TABLE TB_USER COMMENT = '사용자';

 ALTER TABLE TB_USER CHANGE COLUMN User_ID User_ID VARCHAR(20) NOT NULL COMMENT '사용자ID';
 ALTER TABLE TB_USER CHANGE COLUMN Member_Comp_Seq Member_Comp_Seq INTEGER NOT NULL COMMENT '회원사순번';
 ALTER TABLE TB_USER CHANGE COLUMN User_Nm User_Nm VARCHAR(20) NULL COMMENT '사용자명';
 ALTER TABLE TB_USER CHANGE COLUMN User_Phone User_Phone VARCHAR(20) NULL COMMENT '사용전화번호';
 ALTER TABLE TB_USER CHANGE COLUMN User_Part_CD User_Part_CD CHAR(4) NULL COMMENT '사용부서코드';
 ALTER TABLE TB_USER CHANGE COLUMN User_Authority User_Authority CHAR(2) NULL COMMENT '사용자권한';
 ALTER TABLE TB_USER CHANGE COLUMN User_Passwd User_Passwd VARCHAR(255) NULL COMMENT '사용자비밀번호';
 ALTER TABLE TB_USER CHANGE COLUMN Last_Connect_Dt Last_Connect_Dt DATE NULL COMMENT '최종접속일';
 ALTER TABLE TB_USER CHANGE COLUMN Delete_YN Delete_YN CHAR(1) NULL COMMENT '삭제여부';
 ALTER TABLE TB_USER CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_USER CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_USER CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_USER CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 


ALTER TABLE TB_Daily_Stat_Bottle COMMENT = '일별용기통계';

 ALTER TABLE TB_Daily_Stat_Bottle CHANGE COLUMN Stat_Dt Stat_Dt CHAR(10) NOT NULL COMMENT '통계일자';
 ALTER TABLE TB_Daily_Stat_Bottle CHANGE COLUMN Bottle_Input_Count Bottle_Input_Count NUMERIC(5) NULL COMMENT '용기입고수량';
 ALTER TABLE TB_Daily_Stat_Bottle CHANGE COLUMN Bottle_Charge_Count Bottle_Charge_Count NUMERIC(5) NULL COMMENT '용기충전수량';
 ALTER TABLE TB_Daily_Stat_Bottle CHANGE COLUMN Bottle_Sales_Count Bottle_Sales_Count NUMERIC(5) NULL COMMENT '용기판매수량';
 ALTER TABLE TB_Daily_Stat_Bottle CHANGE COLUMN Bottle_Back_Count Bottle_Back_Count NUMERIC(5) NULL COMMENT '용기회수수량';
 ALTER TABLE TB_Daily_Stat_Bottle CHANGE COLUMN Bottle_Discard_Count Bottle_Discard_Count NUMERIC(5) NULL COMMENT '용기페기수량';
 ALTER TABLE TB_Daily_Stat_Bottle CHANGE COLUMN Create_Id Create_Id VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Daily_Stat_Bottle CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 

ALTER TABLE TB_Daily_Stat_Customer COMMENT = '일별거래처통계';

 ALTER TABLE TB_Daily_Stat_Customer CHANGE COLUMN Customer_ID Customer_ID INTEGER NOT NULL COMMENT '거래처ID';
 ALTER TABLE TB_Daily_Stat_Customer CHANGE COLUMN Stat_Dt Stat_Dt CHAR(10) NOT NULL COMMENT '통계일자';
 ALTER TABLE TB_Daily_Stat_Customer CHANGE COLUMN Order_Count Order_Count INTEGER NULL COMMENT '주문수량';
 ALTER TABLE TB_Daily_Stat_Customer CHANGE COLUMN Order_Amount Order_Amount NUMERIC(10) NULL COMMENT '주문금액';
 ALTER TABLE TB_Daily_Stat_Customer CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Daily_Stat_Customer CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 

ALTER TABLE TB_Daily_Stat_Product COMMENT = '일별상품통계';

 ALTER TABLE TB_Daily_Stat_Product CHANGE COLUMN Product_ID Product_ID INTEGER NOT NULL COMMENT '상품ID';
 ALTER TABLE TB_Daily_Stat_Product CHANGE COLUMN Stat_Dt Stat_Dt CHAR(10) NOT NULL COMMENT '통계일자';
 ALTER TABLE TB_Daily_Stat_Product CHANGE COLUMN Order_Count Order_Count INTEGER NULL COMMENT '주문수량';
 ALTER TABLE TB_Daily_Stat_Product CHANGE COLUMN Order_Amount Order_Amount NUMERIC(10) NULL COMMENT '주문금액';
 ALTER TABLE TB_Daily_Stat_Product CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Daily_Stat_Product CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 

ALTER TABLE TB_Daily_Stat_Order COMMENT = '일별주문통계';

 ALTER TABLE TB_Daily_Stat_Order CHANGE COLUMN Stat_Dt Stat_Dt CHAR(10) NOT NULL COMMENT '통계일자';
 ALTER TABLE TB_Daily_Stat_Order CHANGE COLUMN Order_Total_Count Order_Total_Count NUMERIC(2) NULL COMMENT '총주문건수';
 ALTER TABLE TB_Daily_Stat_Order CHANGE COLUMN Order_Total_Amount Order_Total_Amount NUMERIC(10) NULL COMMENT '총주문금액';
 ALTER TABLE TB_Daily_Stat_Order CHANGE COLUMN Order_Product_Count Order_Product_Count NUMERIC(2) NULL COMMENT '상품주문수';
 ALTER TABLE TB_Daily_Stat_Order CHANGE COLUMN Order_Cancel_Count Order_Cancel_Count NUMERIC(2) NULL COMMENT '취소주문수';
 ALTER TABLE TB_Daily_Stat_Order CHANGE COLUMN Order_Retrieved_Count Order_Retrieved_Count NUMERIC(2) NULL COMMENT '회수주문수';
 ALTER TABLE TB_Daily_Stat_Order CHANGE COLUMN Order_Check_Count Order_Check_Count NUMERIC(2) NULL COMMENT '점검요청수';
 ALTER TABLE TB_Daily_Stat_Order CHANGE COLUMN Order_Etc_Count Order_Etc_Count NUMERIC(2) NULL COMMENT '기타요청수';
 ALTER TABLE TB_Daily_Stat_Order CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Daily_Stat_Order CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Daily_Stat_Order CHANGE COLUMN Order_Auto_Count Order_Auto_Count NUMERIC(2) NULL COMMENT '자동주문수';
 

ALTER TABLE TB_Monthly_Stat_Bottle COMMENT = '월별용기통계';

 ALTER TABLE TB_Monthly_Stat_Bottle CHANGE COLUMN Stat_Dt Stat_Dt CHAR(7) NOT NULL COMMENT '통계일자';
 ALTER TABLE TB_Monthly_Stat_Bottle CHANGE COLUMN Bottle_Input_Count Bottle_Input_Count NUMERIC(5) NULL COMMENT '용기입고수량';
 ALTER TABLE TB_Monthly_Stat_Bottle CHANGE COLUMN Bottle_Charge_Count Bottle_Charge_Count NUMERIC(5) NULL COMMENT '용기충전수량';
 ALTER TABLE TB_Monthly_Stat_Bottle CHANGE COLUMN Bottle_Sales_Count Bottle_Sales_Count NUMERIC(5) NULL COMMENT '용기판매수량';
 ALTER TABLE TB_Monthly_Stat_Bottle CHANGE COLUMN Bottle_Back_Count Bottle_Back_Count NUMERIC(5) NULL COMMENT '용기회수수량';
 ALTER TABLE TB_Monthly_Stat_Bottle CHANGE COLUMN Bottle_Discard_Count Bottle_Discard_Count NUMERIC(5) NULL COMMENT '용기폐기수량';
 ALTER TABLE TB_Monthly_Stat_Bottle CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Monthly_Stat_Bottle CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 

ALTER TABLE TB_Monthly_Stat_Customer COMMENT = '월별거래처통계';

 ALTER TABLE TB_Monthly_Stat_Customer CHANGE COLUMN Stat_Dt Stat_Dt CHAR(7) NOT NULL COMMENT '통계일자';
 ALTER TABLE TB_Monthly_Stat_Customer CHANGE COLUMN Customer_ID Customer_ID INTEGER NULL COMMENT '거래처ID';
 ALTER TABLE TB_Monthly_Stat_Customer CHANGE COLUMN Order_Count Order_Count INTEGER NULL COMMENT '주문수량';
 ALTER TABLE TB_Monthly_Stat_Customer CHANGE COLUMN Order_Amount Order_Amount NUMERIC(10) NULL COMMENT '주문금액';
 ALTER TABLE TB_Monthly_Stat_Customer CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Monthly_Stat_Customer CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 

ALTER TABLE TB_Monthly_Stat_Product COMMENT = '월별상품통계';

 ALTER TABLE TB_Monthly_Stat_Product CHANGE COLUMN Product_ID Product_ID INTEGER NOT NULL COMMENT '상품ID';
 ALTER TABLE TB_Monthly_Stat_Product CHANGE COLUMN Stat_Dt Stat_Dt CHAR(7) NOT NULL COMMENT '통계일자';
 ALTER TABLE TB_Monthly_Stat_Product CHANGE COLUMN Order_Count Order_Count INTEGER NULL COMMENT '주문수량';
 ALTER TABLE TB_Monthly_Stat_Product CHANGE COLUMN Order_Amount Order_Amount NUMERIC(10) NULL COMMENT '주문금액';
 ALTER TABLE TB_Monthly_Stat_Product CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Monthly_Stat_Product CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 

ALTER TABLE TB_Monthly_Stat_Order COMMENT = '월별주문통계';

 ALTER TABLE TB_Monthly_Stat_Order CHANGE COLUMN Stat_Dt Stat_Dt CHAR(7) NOT NULL COMMENT '통계일자';
 ALTER TABLE TB_Monthly_Stat_Order CHANGE COLUMN Order_Total_Count Order_Total_Count NUMERIC(2) NULL COMMENT '총주문건수';
 ALTER TABLE TB_Monthly_Stat_Order CHANGE COLUMN Order_Total_Amount Order_Total_Amount NUMERIC(10) NULL COMMENT '총주문금액';
 ALTER TABLE TB_Monthly_Stat_Order CHANGE COLUMN Order_Product_Count Order_Product_Count NUMERIC(2) NULL COMMENT '상품주문수';
 ALTER TABLE TB_Monthly_Stat_Order CHANGE COLUMN Order_Cancel_Count Order_Cancel_Count NUMERIC(2) NULL COMMENT '취소주문수';
 ALTER TABLE TB_Monthly_Stat_Order CHANGE COLUMN Order_Retrieved_Count Order_Retrieved_Count NUMERIC(2) NULL COMMENT '회수주문수';
 ALTER TABLE TB_Monthly_Stat_Order CHANGE COLUMN Order_Check_Count Order_Check_Count NUMERIC(2) NULL COMMENT '점검요청수';
 ALTER TABLE TB_Monthly_Stat_Order CHANGE COLUMN Order_Etc_Count Order_Etc_Count NUMERIC(2) NULL COMMENT '기타요청수';
 ALTER TABLE TB_Monthly_Stat_Order CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Monthly_Stat_Order CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Monthly_Stat_Order CHANGE COLUMN Order_Auto_Count Order_Auto_Count NUMERIC(2) NULL COMMENT '자동주문수';
 
ALTER TABLE TB_Work_Bottle COMMENT = '업무용기';

 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Work_Report_Seq Work_Report_Seq INTEGER NOT NULL COMMENT '작업일지순번';
 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Bottle_ID Bottle_ID CHAR(10) NOT NULL COMMENT '용기ID';
 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Bottle_BarCD Bottle_BarCD CHAR(10) NULL COMMENT '용기바코드';
 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Customer_ID Customer_ID INTEGER NULL COMMENT '거래처ID';
 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Bottle_Work_Cd Bottle_Work_Cd CHAR(4) NULL COMMENT '용기작업코드';
 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Gas_ID Gas_ID INTEGER NULL COMMENT '가스ID';
 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Product_ID Product_ID INTEGER NULL COMMENT '상품ID';
 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Product_Price_Seq Product_Price_Seq INTEGER NULL COMMENT '상품가격순번';
 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Bottle_Type Bottle_Type CHAR(1) NULL COMMENT '용기타입';
 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Work_Bottle CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 

ALTER TABLE TB_Work_Report COMMENT = '업무일지';

 ALTER TABLE TB_Work_Report CHANGE COLUMN Work_Report_Seq Work_Report_Seq INTEGER NOT NULL COMMENT '작업일지순번';
 ALTER TABLE TB_Work_Report CHANGE COLUMN User_ID User_ID VARCHAR(20) NOT NULL COMMENT '사용자ID';
 ALTER TABLE TB_Work_Report CHANGE COLUMN Order_ID Order_ID INTEGER NOT NULL COMMENT '주문ID';
 ALTER TABLE TB_Work_Report CHANGE COLUMN Customer_ID Customer_ID INTEGER NOT NULL COMMENT '거래처ID';
 ALTER TABLE TB_Work_Report CHANGE COLUMN Work_Dt Work_Dt DATETIME NULL COMMENT '작업일자';
 ALTER TABLE TB_Work_Report CHANGE COLUMN Order_Amount Order_Amount NUMERIC(10) NULL COMMENT '주문금액';
 ALTER TABLE TB_Work_Report CHANGE COLUMN Received_Amount Received_Amount NUMERIC(10) NULL COMMENT '수금액';
 ALTER TABLE TB_Work_Report CHANGE COLUMN Bottle_Type Bottle_Type CHAR(1) NULL COMMENT '용기타입';
 ALTER TABLE TB_Work_Report CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Work_Report CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Work_Report CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Work_Report CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 


ALTER TABLE TB_Schedule COMMENT = '일정';

 ALTER TABLE TB_Schedule CHANGE COLUMN Schedule_Seq Schedule_Seq INTEGER NOT NULL COMMENT '일정순번';
 ALTER TABLE TB_Schedule CHANGE COLUMN User_ID User_ID VARCHAR(20) NOT NULL COMMENT '사용자ID';
 ALTER TABLE TB_Schedule CHANGE COLUMN Schedule_Type Schedule_Type CHAR(1) NULL COMMENT '일정타입';
 ALTER TABLE TB_Schedule CHANGE COLUMN Schedule_Start_Dt Schedule_Start_Dt DATETIME NULL COMMENT '일정시작일자';
 ALTER TABLE TB_Schedule CHANGE COLUMN Schedule_End_Dt Schedule_End_Dt DATETIME NULL COMMENT '일정종료일자';
 ALTER TABLE TB_Schedule CHANGE COLUMN Vacation_Gubun Vacation_Gubun CHAR(1) NULL COMMENT '휴가구분';
 ALTER TABLE TB_Schedule CHANGE COLUMN Delete_YN Delete_YN CHAR(1) NULL COMMENT '삭제여부';
 ALTER TABLE TB_Schedule CHANGE COLUMN Create_ID Create_ID VARCHAR(20) NULL COMMENT '등록자ID';
 ALTER TABLE TB_Schedule CHANGE COLUMN Create_Dt Create_Dt DATETIME NULL COMMENT '등록일자';
 ALTER TABLE TB_Schedule CHANGE COLUMN Update_ID Update_ID VARCHAR(20) NULL COMMENT '수정자ID';
 ALTER TABLE TB_Schedule CHANGE COLUMN Update_Dt Update_Dt DATETIME NULL COMMENT '수정일자';
 ALTER TABLE TB_Schedule CHANGE COLUMN Schedule_Title Schedule_Title VARCHAR(30) NULL COMMENT '일정제목';
 
