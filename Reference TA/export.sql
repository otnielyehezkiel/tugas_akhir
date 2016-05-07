--------------------------------------------------------
--  File created - Thursday-March-31-2016   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence M_COUNTRY_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "M_COUNTRY_ID_SEQ"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence M_REGION_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "M_REGION_ID_SEQ"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence PERUSAHAAN_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "PERUSAHAAN_ID_SEQ"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Table MATERIAL
--------------------------------------------------------

  CREATE TABLE "MATERIAL" 
   (	"MATNR" VARCHAR2(18), 
	"MTART" VARCHAR2(4), 
	"MBRSH" VARCHAR2(15), 
	"MAKTX" VARCHAR2(40), 
	"MEINS" VARCHAR2(3), 
	"MATKL" VARCHAR2(9)
   ) ;
--------------------------------------------------------
--  DDL for Table M_COUNTRY
--------------------------------------------------------

  CREATE TABLE "M_COUNTRY" 
   (	"ID" NUMBER(3,0), 
	"COUNTRY_KEY" VARCHAR2(3)
   ) ;
--------------------------------------------------------
--  DDL for Table M_REGION
--------------------------------------------------------

  CREATE TABLE "M_REGION" 
   (	"ID" NUMBER(3,0), 
	"REGION_NAME" VARCHAR2(32)
   ) ;
--------------------------------------------------------
--  DDL for Table PERUSAHAAN
--------------------------------------------------------

  CREATE TABLE "PERUSAHAAN" 
   (	"ID" NUMBER(20,0), 
	"NPWP" VARCHAR2(2000), 
	"NAMA" VARCHAR2(2000), 
	"ALAMAT" VARCHAR2(2000), 
	"EMAIL" VARCHAR2(2000), 
	"TELEPON" VARCHAR2(2000), 
	"KEKAYAAN_BERSIH" NUMBER(20,2), 
	"CP" VARCHAR2(2000), 
	"CP_HP" VARCHAR2(2000), 
	"USERNAME" VARCHAR2(2000), 
	"PASSWORD" VARCHAR2(2000), 
	"PERTANYAAN" VARCHAR2(2000), 
	"JAWABAN" VARCHAR2(40), 
	"WAKTU_AKTIVASI" DATE, 
	"KOTA" VARCHAR2(2000), 
	"FAX" VARCHAR2(2000), 
	"BOLEH_HAPUS_AHLI" NUMBER DEFAULT 0, 
	"STATE" NUMBER DEFAULT 0, 
	"CREATED_AT" DATE, 
	"UPDATED_AT" DATE, 
	"DPP_NOMOR" VARCHAR2(2000), 
	"DPP_TANGGAL_MULAI" DATE, 
	"DPP_TANGGAL_BERLAKU" DATE, 
	"DPP_STATUS" NUMBER(*,0) DEFAULT 0, 
	"ID_ERP" VARCHAR2(32), 
	"VAT_RNUM" VARCHAR2(20), 
	"REGION_ID" NUMBER(3,0), 
	"COUNTRY_ID" NUMBER(3,0), 
	"TAX_NUMBER" VARCHAR2(16), 
	"FLAG" NUMBER(*,0) DEFAULT 0
   ) ;

---------------------------------------------------
--   DATA FOR TABLE MATERIAL
--   FILTER = none used
---------------------------------------------------
REM INSERTING into MATERIAL
Insert into MATERIAL (MATNR,MTART,MBRSH,MAKTX,MEINS,MATKL) values ('30170010830','ZKBP','Manufacture','BEARING 2305','PC','BEARING');

---------------------------------------------------
--   END DATA FOR TABLE MATERIAL
---------------------------------------------------

---------------------------------------------------
--   DATA FOR TABLE M_COUNTRY
--   FILTER = none used
---------------------------------------------------
REM INSERTING into M_COUNTRY

---------------------------------------------------
--   END DATA FOR TABLE M_COUNTRY
---------------------------------------------------

---------------------------------------------------
--   DATA FOR TABLE M_REGION
--   FILTER = none used
---------------------------------------------------
REM INSERTING into M_REGION

---------------------------------------------------
--   END DATA FOR TABLE M_REGION
---------------------------------------------------

---------------------------------------------------
--   DATA FOR TABLE PERUSAHAAN
--   FILTER = none used
---------------------------------------------------
REM INSERTING into PERUSAHAAN
Insert into PERUSAHAAN (ID,NPWP,NAMA,ALAMAT,EMAIL,TELEPON,KEKAYAAN_BERSIH,CP,CP_HP,USERNAME,PASSWORD,PERTANYAAN,JAWABAN,WAKTU_AKTIVASI,KOTA,FAX,BOLEH_HAPUS_AHLI,STATE,CREATED_AT,UPDATED_AT,DPP_NOMOR,DPP_TANGGAL_MULAI,DPP_TANGGAL_BERLAKU,DPP_STATUS,ID_ERP,VAT_RNUM,REGION_ID,COUNTRY_ID,TAX_NUMBER,FLAG) values (147,'484565435436767','PT AGROTECH INDO','Jakarta','agroindo1234@gmail.com','021 3453536634',85000000,null,null,'agroindo1234@gmail.com','d033e22ae348aeb5660fc2140aec35850c4da997','agroindo1234@gmail.com','8b82fe5023a24a94905eee54c7c71d3a2dcbcf84',to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),'Jakarta',null,0,null,to_timestamp('26-FEB-16 12.01.30.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),to_timestamp('26-FEB-16 03.53.32.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),'414',to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),1,null,null,null,null,null,0);
Insert into PERUSAHAAN (ID,NPWP,NAMA,ALAMAT,EMAIL,TELEPON,KEKAYAAN_BERSIH,CP,CP_HP,USERNAME,PASSWORD,PERTANYAAN,JAWABAN,WAKTU_AKTIVASI,KOTA,FAX,BOLEH_HAPUS_AHLI,STATE,CREATED_AT,UPDATED_AT,DPP_NOMOR,DPP_TANGGAL_MULAI,DPP_TANGGAL_BERLAKU,DPP_STATUS,ID_ERP,VAT_RNUM,REGION_ID,COUNTRY_ID,TAX_NUMBER,FLAG) values (149,'011011110010101','PT ABADI SEJAHTERA','jl Royal State ketintang 6','pt_abadi@ppwjatim.org','0316827999',90900000000,null,null,'pt_abadi@ppwjatim.org','d033e22ae348aeb5660fc2140aec35850c4da997','siapa aku ini','4287a065b002b489139b2884a2c619476bd18025',to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),null,null,0,0,to_timestamp('26-FEB-16 04.04.21.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),to_timestamp('26-FEB-16 04.19.18.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),null,null,null,0,null,null,null,null,null,0);
Insert into PERUSAHAAN (ID,NPWP,NAMA,ALAMAT,EMAIL,TELEPON,KEKAYAAN_BERSIH,CP,CP_HP,USERNAME,PASSWORD,PERTANYAAN,JAWABAN,WAKTU_AKTIVASI,KOTA,FAX,BOLEH_HAPUS_AHLI,STATE,CREATED_AT,UPDATED_AT,DPP_NOMOR,DPP_TANGGAL_MULAI,DPP_TANGGAL_BERLAKU,DPP_STATUS,ID_ERP,VAT_RNUM,REGION_ID,COUNTRY_ID,TAX_NUMBER,FLAG) values (148,'129991231123123','PT MANDIRI JAYA PERKASA','JL. MANYAR 12 SURABAYA','pt_mandiri@ppwjatim.org',null,200890000000,null,null,'pt_mandiri@ppwjatim.org','d033e22ae348aeb5660fc2140aec35850c4da997','APA NAMA PERUSAHAAN','46478282a93969b32588d2400f12ef0fce5880b9',to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),null,null,0,0,to_timestamp('26-FEB-16 03.21.38.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),to_timestamp('26-FEB-16 03.33.07.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),'0102',to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),1,null,null,null,null,null,0);
Insert into PERUSAHAAN (ID,NPWP,NAMA,ALAMAT,EMAIL,TELEPON,KEKAYAAN_BERSIH,CP,CP_HP,USERNAME,PASSWORD,PERTANYAAN,JAWABAN,WAKTU_AKTIVASI,KOTA,FAX,BOLEH_HAPUS_AHLI,STATE,CREATED_AT,UPDATED_AT,DPP_NOMOR,DPP_TANGGAL_MULAI,DPP_TANGGAL_BERLAKU,DPP_STATUS,ID_ERP,VAT_RNUM,REGION_ID,COUNTRY_ID,TAX_NUMBER,FLAG) values (150,'466587587758496','PT CEMERLANG PATI','jl. Basuki Rahmat 82','pt_cemerlang@ppwjatim.org','0318398y9',880000000000,null,null,'pt_cemerlang@ppwjatim.org','d033e22ae348aeb5660fc2140aec35850c4da997','siapa aku ini','4287a065b002b489139b2884a2c619476bd18025',to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),null,null,0,0,to_timestamp('26-FEB-16 04.34.56.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),to_timestamp('26-FEB-16 05.30.54.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),'0103',to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),to_timestamp('26-FEB-17 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),1,null,null,null,null,null,0);
Insert into PERUSAHAAN (ID,NPWP,NAMA,ALAMAT,EMAIL,TELEPON,KEKAYAAN_BERSIH,CP,CP_HP,USERNAME,PASSWORD,PERTANYAAN,JAWABAN,WAKTU_AKTIVASI,KOTA,FAX,BOLEH_HAPUS_AHLI,STATE,CREATED_AT,UPDATED_AT,DPP_NOMOR,DPP_TANGGAL_MULAI,DPP_TANGGAL_BERLAKU,DPP_STATUS,ID_ERP,VAT_RNUM,REGION_ID,COUNTRY_ID,TAX_NUMBER,FLAG) values (151,'336899594698758','PT JAYA KAHURIPAN','jl kahuripan','pt_jaya@ppwjatim.org','083849730009',14000000000,null,null,'pt_jaya@ppwjatim.org','97d38ab363776fdf4948900023a8eeeeedf790f0','siapa aku ini','4287a065b002b489139b2884a2c619476bd18025',to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),null,null,0,0,to_timestamp('26-FEB-16 12.19.57.000000000 PM','DD-MON-RR HH.MI.SS.FF AM'),to_timestamp('26-FEB-16 12.34.01.000000000 PM','DD-MON-RR HH.MI.SS.FF AM'),'0105',to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),1,null,null,null,null,null,0);
Insert into PERUSAHAAN (ID,NPWP,NAMA,ALAMAT,EMAIL,TELEPON,KEKAYAAN_BERSIH,CP,CP_HP,USERNAME,PASSWORD,PERTANYAAN,JAWABAN,WAKTU_AKTIVASI,KOTA,FAX,BOLEH_HAPUS_AHLI,STATE,CREATED_AT,UPDATED_AT,DPP_NOMOR,DPP_TANGGAL_MULAI,DPP_TANGGAL_BERLAKU,DPP_STATUS,ID_ERP,VAT_RNUM,REGION_ID,COUNTRY_ID,TAX_NUMBER,FLAG) values (152,'353253522245354','PT SEJAHTERA LUHUR','jl kulamun asing 88','pt_sejahtera@ppwjatim.org','08576778955',18000000000,null,null,'pt_sejahtera@ppwjatim.org','d033e22ae348aeb5660fc2140aec35850c4da997','siapa aku ini','4287a065b002b489139b2884a2c619476bd18025',to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),null,null,0,0,to_timestamp('26-FEB-16 12.25.21.000000000 PM','DD-MON-RR HH.MI.SS.FF AM'),to_timestamp('26-FEB-16 12.31.21.000000000 PM','DD-MON-RR HH.MI.SS.FF AM'),'0104',to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),to_timestamp('26-FEB-16 12.00.00.000000000 AM','DD-MON-RR HH.MI.SS.FF AM'),1,null,null,null,null,null,0);

---------------------------------------------------
--   END DATA FOR TABLE PERUSAHAAN
---------------------------------------------------
--------------------------------------------------------
--  Constraints for Table M_COUNTRY
--------------------------------------------------------

  ALTER TABLE "M_COUNTRY" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table MATERIAL
--------------------------------------------------------

  ALTER TABLE "MATERIAL" ADD CONSTRAINT "MATERIAL_PK" PRIMARY KEY ("MATNR") ENABLE;
 
  ALTER TABLE "MATERIAL" MODIFY ("MATNR" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table M_REGION
--------------------------------------------------------

  ALTER TABLE "M_REGION" ADD PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table PERUSAHAAN
--------------------------------------------------------

  ALTER TABLE "PERUSAHAAN" ADD CONSTRAINT "ID_PERUSAHAAN_PK" PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  DDL for Index MATERIAL_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "MATERIAL_PK" ON "MATERIAL" ("MATNR") 
  ;
--------------------------------------------------------
--  DDL for Index ID_PERUSAHAAN_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ID_PERUSAHAAN_PK" ON "PERUSAHAAN" ("ID") 
  ;
