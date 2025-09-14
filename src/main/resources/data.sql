-- Criação das categorias
INSERT INTO categoria (nome, descricao) VALUES
('Eletrônicos', 'eletronico em geral'),
('Periféricos', 'Dispositivos de entrada e saída para computadores, como mouse, teclado e webcam.'),
('Computadores/Peças', 'Componentes de computadores e equipamentos, como notebooks, monitores e placas.');

-- Dados iniciais para teste
INSERT INTO produtos (nome, descricao, preco, quantidade, categoria_id) VALUES
('Notebook Dell', 'Notebook Dell Inspiron 15 com 8GB RAM e SSD 256GB', 2500.00, 10, 3),
('Mouse Logitech', 'Mouse óptico sem fio Logitech M170', 45.90, 25, 2),
('Teclado Mecânico', 'Teclado mecânico RGB com switches Cherry MX Blue', 299.99, 5, 2),
('Monitor Samsung', 'Monitor LED 24 polegadas Full HD Samsung', 899.00, 8, 3),
('Webcam HD', 'Webcam HD 1080p com microfone integrado', 129.90, 15, 2);

