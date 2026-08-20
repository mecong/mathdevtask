# High-Performance Java Slot Game Simulator & Mathematical Validator

A high-performance, modular, and extensible 3x3 slot machine simulation engine and mathematical verification tool built in Java 25+ with Virtual Threads, Lombok, and Spock Framework.

---

## 1. Executive Summary & Mathematical Findings

This project implements and validates the exact math model for the **Rubyplay 3x3 Classic Fruit Slot** specified in the assessment PAR sheet.

### Mathematical Specification vs Simulated Results

| Metric | Theoretical Math Model (PAR Sheet) | Exhaustive Verification (191,052 Permutations) | Monte Carlo Simulation (10,000,000 Spins) |
| :--- | :--- | :--- | :--- |
| **Total Outcomes** | $61 \times 58 \times 54 = 191,052$ | $191,052$ | $10,000,000$ |
| **Total Wager** | $1,910,520$ credits | $1,910,520$ credits | $100,000,000$ credits |
| **Total Payout** | $1,783,625$ credits | $1,783,625$ credits | $93,508,625$ credits |
| **Return to Player (RTP)** | **93.36%** ($93.358091\%$) | **93.358091%** | **93.51%** ($93.508625\%$) |
| **Standard Deviation ($\sigma_{\text{credits}}$)** | $48.676$ credits | $48.676397$ credits | $49.001063$ credits |
| **Standard Deviation ($\sigma_{\text{bet units}}$)** | **4.868** bet units | **4.867640** bet units | **4.900106** bet units |
| **Hit Frequency** | $13.32\%$ ($25,449 / 191,052$) | $13.32\%$ | $13.32\%$ ($1,332,414 / 10,000,000$) |
| **95% Confidence Interval** | — | — | **[93.2049% - 93.8123%]** |
| **99% Confidence Interval** | — | — | **[93.1095% - 93.9078%]** |
| **Execution Wall Time** | — | **86 ms** | **625 ms** |
| **Throughput** | — | **2.22M spins/sec** | **16.00M spins/sec** |

### Per-Symbol Payout & Hit Distribution

| Symbol Code | Symbol Name | Theoretical Hits (PAR Sheet) | Total Line Hits (Sheet $\times$ 5) | Simulated Hits (10M Runs) | Theoretical Return % | Simulated Return % |
| :---: | :--- | :---: | :---: | :---: | :---: | :---: |
| `W1` | Wild | 1 | 5 | 293 | 0.52% | 0.59% |
| `H1` | Seven | 17 | 85 | 4,522 | 3.56% | 3.62% |
| `H2` | Bell | 149 | 745 | 39,255 | 19.50% | 19.63% |
| `H3` | Bar | 1,099 | 5,495 | 287,066 | 23.01% | 22.97% |
| `L1` | Banana | 485 | 2,425 | 126,446 | 6.35% | 6.32% |
| `L2` | Orange | 2,351 | 11,755 | 615,320 | 12.31% | 12.31% |
| `L3` | Plum | 1,403 | 7,015 | 367,041 | 5.51% | 5.51% |
| `L4` | Cherry | 539 | 2,695 | 141,393 | 1.41% | 1.41% |
| `SCA` | Scatter | 75 | 2,025 | 105,820 | 21.20% | 21.16% |

---

## 2. Game Mechanics & Mathematical Model

- **Matrix**: 3 Reels $\times$ 3 Rows ($3 \times 3$ grid).
- **Reel Lengths**:
  - Reel 0: 61 symbols
  - Reel 1: 58 symbols
  - Reel 2: 54 symbols
  - Combinatorial Outcome Space: $61 \times 58 \times 54 = 191,052$ combinations.
- **Paylines (5)**:
  1. Top Horizontal: `[(0,0), (0,1), (0,2)]`
  2. Middle Horizontal: `[(1,0), (1,1), (1,2)]`
  3. Bottom Horizontal: `[(2,0), (2,1), (2,2)]`
  4. Diagonal Top-Left to Bottom-Right: `[(0,0), (1,1), (2,2)]`
  5. Diagonal Bottom-Left to Top-Right: `[(2,0), (1,1), (0,2)]`
- **Symbols & Payout Rules**:
  - `W1` (Wild): Substitutes for all symbols except `SCA`. 3x `W1` pays 2000 credits.
  - `H1` (Seven): 3x pays 800 credits.
  - `H2` (Bell): 3x pays 500 credits.
  - `H3` (Bar): 3x pays 80 credits.
  - `L1` (Banana): 3x pays 50 credits.
  - `L2` (Orange): 3x pays 20 credits.
  - `L3` (Plum): 3x pays 15 credits.
  - `L4` (Cherry): 3x pays 10 credits.
  - `SCA` (Scatter): 3 anywhere on the $3 \times 3$ grid pays 200 credits.
- **Round Cost / Bet**: 10 credits.

---

## 3. Statistical Methodology & Formulas

### Return to Player (RTP)
$$\text{RTP} = \frac{\sum_{i=1}^N W_i}{\sum_{i=1}^N B_i} = \frac{\bar{W}}{\text{Bet}}$$

### Online Numerically Stable Variance (Welford's Algorithm & Parallel Reduction)
To prevent numerical cancellation and catastrophic precision loss across 10,000,000 iterations, we implement Welford's online variance algorithm:

For each single observation $x_n$:
$$\delta = x_n - M_{n-1}$$
$$M_n = M_{n-1} + \frac{\delta}{n}$$
$$M_{2,n} = M_{2,n-1} + \delta(x_n - M_n)$$
$$s^2 = \frac{M_{2,n}}{n-1}, \quad \sigma = \sqrt{s^2}$$

For combining parallel worker thread accumulators (Chan et al.):
$$n = n_A + n_B, \quad \delta = M_B - M_A$$
$$M = M_A + \delta \cdot \frac{n_B}{n}$$
$$M_2 = M_{2,A} + M_{2,B} + \delta^2 \cdot \frac{n_A \cdot n_B}{n}$$

### Standard Deviation in Bet Units
$$\sigma_{\text{bet units}} = \frac{\sigma_{\text{credits}}}{\text{Bet}}$$

### Standard Error & Confidence Intervals
$$\text{SE} = \frac{s}{\sqrt{N}}, \quad \text{SE}_{\text{RTP}} = \frac{\text{SE}}{\text{Bet}} \times 100$$
$$\text{CI}_{95\%} = \left[\text{RTP} - 1.95996 \cdot \text{SE}_{\text{RTP}}, \; \text{RTP} + 1.95996 \cdot \text{SE}_{\text{RTP}}\right]$$
$$\text{CI}_{99\%} = \left[\text{RTP} - 2.57583 \cdot \text{SE}_{\text{RTP}}, \; \text{RTP} + 2.57583 \cdot \text{SE}_{\text{RTP}}\right]$$

---

## 4. Architecture & Engineering Highlights

```
src/main/java/com/rubyplay/slot/
├── Main.java                          # CLI Application Entrypoint
├── cli/
│   └── SlotSimulationCommand.java     # Picocli CLI with customizable flags
├── config/
│   ├── AppProperties.java             # 12-factor Centralized Configuration
│   ├── DefaultGameConfigFactory.java  # PAR sheet programmatic factory
│   └── GameConfigLoader.java          # Jackson-based JSON configuration loader
├── engine/
│   └── SlotEngine.java                # Fast simulation and spin orchestrator
├── evaluator/
│   ├── WinEvaluator.java              # Extensible win evaluation interface
│   └── StandardWinEvaluator.java      # Left-to-right line + scatter evaluator
├── model/
│   ├── Symbol.java                    # Symbol enum (Wild, Scatter flags, matches)
│   ├── ReelStrip.java                 # Immutable circular reel strips
│   ├── Payline.java                   # Trajectory offsets
│   ├── PayTable.java                  # Fast array-indexed payout matrix
│   ├── Grid.java                      # 2D symbol matrix view
│   ├── LineWin.java                   # Line win record
│   ├── ScatterWin.java                # Scatter win record
│   ├── SpinOutcome.java               # Comprehensive round outcome
│   └── GameConfig.java                # Master game configuration container
├── simulation/
│   ├── Simulator.java                 # Simulator interface
│   ├── VirtualThreadMonteCarloSimulator.java # Lock-free Virtual Thread parallel runner
│   └── ExhaustiveCombinatorialValidator.java # 191,052 exact outcome solver
└── stats/
    ├── OnlineVarianceAccumulator.java # Welford & Chan parallel variance
    ├── SimulationStatsAccumulator.java# Aggregator and report builder
    ├── SimulationReport.java          # Immutable report DTO
    ├── SymbolStat.java                # Per-symbol breakdown
    └── ReportFormatter.java           # ASCII report renderer
```

- **Java Virtual Threads (`java.lang.VirtualThread`)**: Leverages lightweight virtual threads (`Executors.newVirtualThreadPerTaskExecutor()`) to achieve zero thread contention and ~16,000,000 spins/sec throughput.
- **Zero-Allocation Hot Path**: Pre-cached symbol and payline arrays with array-indexed lookups avoid heap allocations during high-speed Monte Carlo simulations.
- **Decoupled & Extensible**: New matrix dimensions ($5 \times 3$, $5 \times 4$), 243 ways-to-win, or cascading win mechanics can be introduced simply by implementing `WinEvaluator`.

---

## 5. Build, Test & Run Instructions

### Prerequisites
- **Java 25+** (e.g. OpenJDK 25, 26)
- **Maven 3.8+**

### Building and Testing with Spock
Run the complete unit and integration test suite written in **Spock (Groovy)**:
```bash
mvn clean test
```

### Running the Application

#### 1. Full 10,000,000 Round Simulation with Virtual Threads
```bash
mvn exec:java -Dexec.args="--rounds 10000000 --virtual-threads"
```

#### 2. Exact Theoretical Validation + 10M Simulation
```bash
mvn exec:java -Dexec.args="--exact --rounds 10000000"
```

#### 3. Custom Number of Rounds and Workers
```bash
mvn exec:java -Dexec.args="--rounds 50000000 --threads 16"
```

#### 4. Custom JSON Game Configuration File
```bash
mvn exec:java -Dexec.args="--config path/to/custom_game.json --rounds 10000000"
```

#### 5. CLI Help
```bash
mvn exec:java -Dexec.args="--help"
```

---

## 6. Spock Test Suite Overview

All tests are located in `src/test/groovy/com/rubyplay/slot/...`:

1. `SymbolSpec`: Symbol parsing, wild substitution matrix, scatter properties.
2. `ReelStripSpec`: Circular wrap-around indexing, boundary checks, visible window extraction.
3. `PaylineSpec`: Row offset lookups and validations.
4. `PayTableSpec`: Fast array lookups, non-winning combinations, null-safety.
5. `GridSpec`: Grid construction from reel strips and stop positions, scatter counting.
6. `StandardWinEvaluatorSpec`: Verification of all 4 PAR sheet test examples, wild substitution priorities, scatter bonuses.
7. `GameConfigLoaderSpec`: JSON deserialization and parity with programmatic factory.
8. `OnlineVarianceAccumulatorSpec`: Welford's algorithm and Chan parallel reduction mathematical precision.
9. `ExhaustiveCombinatorialValidatorSpec`: Mathematical proof of theoretical **93.358091% RTP**, exact symbol hits, and standard deviation over all 191,052 combinations.
10. `VirtualThreadMonteCarloSimulatorSpec`: Parallel 1,000,000 spin convergence and confidence interval verification.
