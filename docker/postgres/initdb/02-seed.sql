-- Dados de exemplo para a aula

INSERT INTO users (first_name, last_name, email, phone, avatar_url) VALUES
    ('Maria',   'Lima',    'maria@example.com',   NULL,                 'https://i.pravatar.cc/150?u=1'),
    ('Joao',    'Calixto', 'joao@example.com',    '+55 11 99999-0000',  'https://i.pravatar.cc/150?u=2'),
    ('Linus',   'Torvalds','linus@kernel.org',    NULL,                 'https://i.pravatar.cc/150?u=3'),
    ('Josh',    'Long',    'josh@spring.io',      '+1 415 555-0100',    'https://i.pravatar.cc/150?u=4'),
    ('Ada',     'Lovelace','ada@analytical.eng',  NULL,                 'https://i.pravatar.cc/150?u=5');

INSERT INTO repos (user_id, name, description, stars, language) VALUES
    (1, 'aula-spring-boot-4', 'Material de estudo Spring Boot 4',         12,   'Java'),
    (2, 'demo-projects',      'Projetos pessoais',                         3,   'Java'),
    (3, 'linux',              'Linux kernel source tree',                  175000, 'C'),
    (4, 'spring-petclinic',   'Sample Spring Boot application',            7500, 'Java'),
    (4, 'spring-ai',          'Spring AI project',                         3200, 'Java'),
    (5, 'analytical-engine',  'Notes on the Analytical Engine',            42,   'English');

INSERT INTO chat_messages (conversation_id, role, content) VALUES
    ('demo-001', 'system',    'Voce e um assistente tecnico amigavel.'),
    ('demo-001', 'user',      'O que e Spring Boot 4?'),
    ('demo-001', 'assistant', 'Spring Boot 4 e a versao GA lancada em nov/2025...');
