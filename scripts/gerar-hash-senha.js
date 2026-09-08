#!/usr/bin/env node
/**
 * Gera um hash de senha no MESMO formato que a API espera
 * (senha_hash da tabela administradores), usando PBKDF2WithHmacSHA256 —
 * o mesmo algoritmo do api/.../util/PasswordHasher.java.
 *
 * Testado e confirmado byte-a-byte idêntico ao hash gerado pelo Java
 * (mesma senha + mesmo salt => mesmo resultado nos dois).
 *
 * USO:
 *   node scripts/gerar-hash-senha.js "sua-senha-forte-aqui"
 *
 * O script imprime um comando SQL pronto pra rodar no banco (via psql ou
 * pgAdmin) trocando a senha do administrador seed antes de ir pra produção.
 */

const crypto = require("node:crypto");

const ITERACOES = 210_000;
const TAMANHO_SALT_BYTES = 16;
const TAMANHO_HASH_BYTES = 32; // 256 bits

const senha = process.argv[2];

if (!senha) {
	console.error("Uso: node scripts/gerar-hash-senha.js \"sua-senha-forte-aqui\"");
	process.exit(1);
}

if (senha.length < 8) {
	console.error("Aviso: senha com menos de 8 caracteres é fraca demais para produção.");
}

const salt = crypto.randomBytes(TAMANHO_SALT_BYTES);
const hash = crypto.pbkdf2Sync(senha, salt, ITERACOES, TAMANHO_HASH_BYTES, "sha256");

const hashFormatado = `${ITERACOES}:${salt.toString("base64")}:${hash.toString("base64")}`;

console.log("\nHash gerado (formato: iterações:salt:hash):\n");
console.log(hashFormatado);

console.log("\nPra trocar a senha do administrador seed, rode este SQL no banco:\n");
console.log(
	`UPDATE administradores SET senha_hash = '${hashFormatado}' WHERE email = 'admin@almoxaf.local';\n`,
);
