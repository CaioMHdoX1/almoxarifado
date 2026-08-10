import { describe, expect, it } from "vitest";
import { cn, formatCpf, somenteDigitos } from "@/lib/utils";

describe("cn", () => {
  it("junta classes ignorando valores falsy", () => {
    expect(cn("a", false, "b", undefined, null, "c")).toBe("a b c");
  });
});

describe("somenteDigitos", () => {
  it("remove tudo que não for número", () => {
    expect(somenteDigitos("111.222.333-44")).toBe("11122233344");
  });
});

describe("formatCpf", () => {
  it("formata 11 dígitos como CPF", () => {
    expect(formatCpf("11122233344")).toBe("111.222.333-44");
  });

  it("devolve o valor original se não tiver 11 dígitos", () => {
    expect(formatCpf("123")).toBe("123");
  });
});
