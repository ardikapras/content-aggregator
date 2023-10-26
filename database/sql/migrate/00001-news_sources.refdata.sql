--liquibase formatted sql
--changeset ardikapras:news_sources.refdata-2 logicalFilePath:path-independent

-- Antara
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara News Terkini', 'https://www.antaranews.com/rss/terkini.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara News Galeri Photo', 'https://www.antaranews.com/rss/photo.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara News Video', 'https://www.antaranews.com/rss/video.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Top News', 'https://www.antaranews.com/rss/top-news.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Politik', 'https://www.antaranews.com/rss/politik.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Hukum', 'https://www.antaranews.com/rss/hukum.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Ekonomi', 'https://www.antaranews.com/rss/ekonomi.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Metro', 'https://www.antaranews.com/rss/metro.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Sepakbola', 'https://www.antaranews.com/rss/sepakbola.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Olahraga', 'https://www.antaranews.com/rss/olahraga.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Humaniora', 'https://www.antaranews.com/rss/humaniora.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Lifestyle', 'https://www.antaranews.com/rss/lifestyle.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Hiburan', 'https://www.antaranews.com/rss/hiburan.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Dunia', 'https://www.antaranews.com/rss/dunia.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Infografik', 'https://www.antaranews.com/rss/infografik.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Tekno', 'https://www.antaranews.com/rss/tekno.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Otomotif', 'https://www.antaranews.com/rss/otomotif.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Warta Bumi', 'https://www.antaranews.com/rss/warta-bumi.xml','ANTARA');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('Antara Rilis Pers', 'https://www.antaranews.com/rss/rilis-pers.xml','ANTARA');

-- CNBC Indonesia
insert into news_sources (name, endpoint_url, parsing_strategy) values ('CNBC ID - Market', 'https://www.cnbcindonesia.com/market/rss','CNBC');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('CNBC ID - News', 'https://www.cnbcindonesia.com/news/rss','CNBC');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('CNBC ID - Entrepreneur', 'https://www.cnbcindonesia.com/entrepreneur/rss','CNBC');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('CNBC ID - Syariah', 'https://www.cnbcindonesia.com/syariah/rss','CNBC');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('CNBC ID - Tech', 'https://www.cnbcindonesia.com/tech/rss','CNBC');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('CNBC ID - Lifestyle', 'https://www.cnbcindonesia.com/lifestyle/rss','CNBC');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('CNBC ID - Opini', 'https://www.cnbcindonesia.com/opini/rss','CNBC');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('CNBC ID - My Money', 'https://www.cnbcindonesia.com/mymoney/rss','CNBC');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('CNBC ID - Cuap Cuap Cuan', 'https://www.cnbcindonesia.com/cuap-cuap-cuan/rss','CNBC');
insert into news_sources (name, endpoint_url, parsing_strategy) values ('CNBC ID - Research', 'https://www.cnbcindonesia.com/research/rss','CNBC');
