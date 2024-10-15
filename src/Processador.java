import java.util.Arrays;

public class Processador {
	public String instrucoes[], instrucao_busca[];
	public int[] registradores, memoria, instrucao_decodificacao, instrucao_execucao, instrucao_memoria, instrucao_wb;
	public int pc, acabou;
	public boolean branckTaken = false;

	public Processador(String instr[]) {
		this.registradores = new int[32];
		this.memoria = new int[128];
		this.instrucoes = instr;
		this.pc = 0;
		this.instrucao_busca = new String[8];
		this.instrucao_decodificacao = new int[8];
		this.instrucao_execucao = new int[8];
		this.instrucao_memoria = new int[8];
		this.instrucao_wb = new int[8];
		this.acabou = 0;

		// Inicialização da memória
		this.memoria[0] = -1;
		this.memoria[1] = 10;
		this.memoria[2] = 1;
		
		this.instrucao_decodificacao[0] = 999999;
		this.instrucao_execucao[0] = 999999;
		this.instrucao_memoria[0] = 999999;
		this.instrucao_wb[0] = 999999;
	}

	public void busca() {
		if(pc < instrucoes.length) {
			instrucao_busca = instrucoes[pc].split(" ");
			pc++;
			System.out.println("Instrucao buscada: " + instrucao_busca[0]);
			System.out.println("PC: " + pc);
			branckTaken = false;
		}
	}

	public void decodificacao() {
		if(instrucao_busca[0] != null && !branckTaken) {
			switch (instrucao_busca[0]) {
				case "lw": {
					instrucao_decodificacao[0] = 0;
					instrucao_decodificacao[1] = Integer.parseInt(instrucao_busca[1]); // R origem
					instrucao_decodificacao[2] = Integer.parseInt(instrucao_busca[2]); // R destino
					instrucao_decodificacao[3] = Integer.parseInt(instrucao_busca[3]); // offset
					break;
				}
				case "sw": {
					instrucao_decodificacao[0] = 1;
					instrucao_decodificacao[1] = Integer.parseInt(instrucao_busca[1]);
					instrucao_decodificacao[2] = Integer.parseInt(instrucao_busca[2]);
					instrucao_decodificacao[3] = Integer.parseInt(instrucao_busca[3]);
					break;
				}
				case "add": {
					instrucao_decodificacao[0] = 2;
					instrucao_decodificacao[1] = Integer.parseInt(instrucao_busca[1]);
					instrucao_decodificacao[2] = Integer.parseInt(instrucao_busca[2]);
					instrucao_decodificacao[3] = Integer.parseInt(instrucao_busca[3]);
					break;
				}
				case "sub": {
					instrucao_decodificacao[0] = 3;
					instrucao_decodificacao[1] = Integer.parseInt(instrucao_busca[1]);
					instrucao_decodificacao[2] = Integer.parseInt(instrucao_busca[2]);
					instrucao_decodificacao[3] = Integer.parseInt(instrucao_busca[3]);
					break;
				}
				case "beq": {
					instrucao_decodificacao[0] = 4;
					instrucao_decodificacao[1] = Integer.parseInt(instrucao_busca[1]);
					instrucao_decodificacao[2] = Integer.parseInt(instrucao_busca[2]);
					instrucao_decodificacao[3] = Integer.parseInt(instrucao_busca[3]);
					break;
				}
				case "noop": {
					instrucao_decodificacao[0] = 5;
					break;
				}
				case "done": {
					instrucao_decodificacao[0] = 6;
					break;
				}
				default: 
					throw new IllegalArgumentException("Instrução incorreta: " + instrucao_busca[0]);
			}
			instrucao_execucao = instrucao_decodificacao.clone();
		}
	}

	public void execucao() {
		if(instrucao_execucao[0] != 999999) {
			System.out.println("Executando instrução: " + Arrays.toString(instrucao_execucao));
			switch(instrucao_execucao[0]) {
				case(0): // lw
					instrucao_execucao[4] = registradores[instrucao_execucao[1]] + instrucao_execucao[3];
					System.out.println("LW: Endereço de memória: " + instrucao_execucao[4]);
					break;
				case(1): // sw
					instrucao_execucao[4] = registradores[instrucao_execucao[1]] + instrucao_execucao[3];
					System.out.println("SW: Endereço de memória: " + instrucao_execucao[4] + ", Valor: " + registradores[instrucao_execucao[2]]);
					break;
				case(2): // add
					instrucao_execucao[5] = registradores[instrucao_execucao[1]] + registradores[instrucao_execucao[2]];
					System.out.println("ADD: Resultado: " + instrucao_execucao[5]);
					break;
				case(3): // sub
					instrucao_execucao[5] = registradores[instrucao_execucao[1]] - registradores[instrucao_execucao[2]];
					System.out.println("SUB: Resultado: " + instrucao_execucao[5]);
					break;
				case(4): // beq
					if(registradores[instrucao_execucao[1]] == registradores[instrucao_execucao[2]]) {
						pc = instrucao_execucao[3] - 1;
						instrucao_execucao[0] = 999999;
						branckTaken = true; 
						System.out.println("Tomado, pc: " + pc);
					} else {
						System.out.println("Não tomado");
					}
					break;
				case(5)://noop
					break;
				case(6)://halt
					break;
			}
			instrucao_memoria = instrucao_execucao.clone();
		}
	}

	public void memoria() {
		if(instrucao_memoria[0] != 999999) {
			switch(instrucao_memoria[0]) {
				case 0: // lw
					instrucao_memoria[6] = memoria[instrucao_memoria[4]];
					break;
				case 1: // sw
					
					break;
				default:
					break;
			}
			instrucao_wb = instrucao_memoria.clone();
		}
	}

	public void writeback() {
		if(instrucao_wb[0] != 999999) {
			switch(instrucao_wb[0]) {
				case 0: //lw
					registradores[instrucao_wb[2]] = instrucao_wb[6];
					break;
				case 1: //sw
					// Não há necessidade de write back para sw
					break;
				case 2: // add
					registradores[instrucao_wb[3]] = instrucao_wb[5];
					break;				
				case 3: // sub
					registradores[instrucao_wb[3]] = instrucao_wb[5];
					break;
				case 4: // beq
					// Não há necessidade de write back para beq
					break;
				case 5: // noop
					// Não há necessidade de write back para noop
					break;
				case 6: // halt
					acabou = 1;
					System.out.println("Done: Programa finalizado.");
					break;
				default:
					break;
			}
		}
	}

	public void imprimirEstado() {
		System.out.println("Estado dos Registradores: " + Arrays.toString(registradores));
		System.out.println("Estado da Memória: " + Arrays.toString(memoria));
		System.out.println("----------------------------------------------------");
		System.out.println();
	}
}