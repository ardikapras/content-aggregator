--liquibase formatted sql
--changeset ardikapras:news_sources.refdata-2 logicalFilePath:path-independent

-- Antara
insert into news_sources (name, endpoint_url) values ('Antara News Terkini', 'https://www.antaranews.com/rss/terkini.xml');
insert into news_sources (name, endpoint_url) values ('Antara News Galeri Photo', 'https://www.antaranews.com/rss/photo.xml');
insert into news_sources (name, endpoint_url) values ('Antara News Video', 'https://www.antaranews.com/rss/video.xml');
insert into news_sources (name, endpoint_url) values ('Antara Top News', 'https://www.antaranews.com/rss/top-news.xml');
insert into news_sources (name, endpoint_url) values ('Antara Politik', 'https://www.antaranews.com/rss/politik.xml');
insert into news_sources (name, endpoint_url) values ('Antara Hukum', 'https://www.antaranews.com/rss/hukum.xml');
insert into news_sources (name, endpoint_url) values ('Antara Ekonomi', 'https://www.antaranews.com/rss/ekonomi.xml');
insert into news_sources (name, endpoint_url) values ('Antara Metro', 'https://www.antaranews.com/rss/metro.xml');
insert into news_sources (name, endpoint_url) values ('Antara Sepakbola', 'https://www.antaranews.com/rss/sepakbola.xml');
insert into news_sources (name, endpoint_url) values ('Antara Olahraga', 'https://www.antaranews.com/rss/olahraga.xml');
insert into news_sources (name, endpoint_url) values ('Antara Humaniora', 'https://www.antaranews.com/rss/humaniora.xml');
insert into news_sources (name, endpoint_url) values ('Antara Lifestyle', 'https://www.antaranews.com/rss/lifestyle.xml');
insert into news_sources (name, endpoint_url) values ('Antara Hiburan', 'https://www.antaranews.com/rss/hiburan.xml');
insert into news_sources (name, endpoint_url) values ('Antara Dunia', 'https://www.antaranews.com/rss/dunia.xml');
insert into news_sources (name, endpoint_url) values ('Antara Infografik', 'https://www.antaranews.com/rss/infografik.xml');
insert into news_sources (name, endpoint_url) values ('Antara Tekno', 'https://www.antaranews.com/rss/tekno.xml');
insert into news_sources (name, endpoint_url) values ('Antara Otomotif', 'https://www.antaranews.com/rss/otomotif.xml');
insert into news_sources (name, endpoint_url) values ('Antara Warta Bumi', 'https://www.antaranews.com/rss/warta-bumi.xml');
insert into news_sources (name, endpoint_url) values ('Antara Rilis Pers', 'https://www.antaranews.com/rss/rilis-pers.xml');

-- CNBC Indonesia
insert into news_sources (name, endpoint_url) values ('CNBC ID - Market', 'https://www.cnbcindonesia.com/market/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - News', 'https://www.cnbcindonesia.com/news/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Entrepreneur', 'https://www.cnbcindonesia.com/entrepreneur/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Syariah', 'https://www.cnbcindonesia.com/syariah/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Tech', 'https://www.cnbcindonesia.com/tech/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Lifestyle', 'https://www.cnbcindonesia.com/lifestyle/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Opini', 'https://www.cnbcindonesia.com/opini/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - My Money', 'https://www.cnbcindonesia.com/mymoney/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Cuap Cuap Cuan', 'https://www.cnbcindonesia.com/cuap-cuap-cuan/rss');
insert into news_sources (name, endpoint_url) values ('CNBC ID - Research', 'https://www.cnbcindonesia.com/research/rss');
