# Technical Assessment: Java Slot Game Simulator

Welcome to our technical assessment! This challenge is designed to evaluate your software engineering practices, object-oriented design skills, and ability to translate mathematical specifications into a high-performance simulation.

You will be building a core simulation engine for a classic 3x3 slot machine in Java.

---

## 1. Domain Terminology Guide
If you haven't worked in the iGaming (online gambling) industry before, some of the mathematical and game concepts might be new. Here is a quick primer to get you up to speed:

### Return to Player (RTP)
*   **What it is:** The theoretical percentage of total wagered money that a game pays back to players over a long period.
*   **The Formula:** 
    $$\text{RTP} = \frac{\text{Total Win Amount}}{\text{Total Bet Amount}}$$
*   **Why it matters:** If players bet a total of $10,000,000 on a game with a $96\%$ RTP, the game should theoretically return $9,600,000 in wins, leaving a $4\%$ "house edge".

### PAR Sheet (Pay Table and Reel Strips)
*   **What it is:** The foundational design document of any slot game. It defines the exact math model of the game, including the symbol lists, the exact sequence of symbols on each reel (strips), the paylines, and the payout values for winning combinations.
*   **Note:** The specific PAR sheet containing our math model is provided as a separate document (`Task math model`).

### Reel Strips and Stop Positions
*   **Reel Strip:** A sequence of symbols representing a physical or virtual reel.
*   **Stop Position:** When a reel stops, it settles on a specific index on the strip. Since the game grid displays a window (e.g., 3 rows), stopping a reel at index $N$ will display the symbols at $N$, $N+1$, and $N+2$ in that column of the grid.

### Paylines
*   Paths across the grid (horizontal, diagonal, or custom shapes) where winning combinations are evaluated. A winning combination (e.g., "3 of a kind") must land on these specific lines to trigger a payout.

### Standard Deviation & Volatility
*   **Standard Deviation:** A statistical measure of the dispersion of game round payouts relative to the average bet. It quantifies the "swing" or volatility of the game. A high standard deviation means large, infrequent wins (high risk/reward), while a low standard deviation means small, frequent wins.

---

## 2. Project Requirements

Your task is to implement a high-performance, command-line Java application that simulates playing the game defined in the accompanying math model.

### Core Objectives

1.  **Model the Game Engine:**
    *   Design clean, extensible domain models representing the game configuration (reels, symbols, paylines, paytable).
    *   Load or parse the game configuration from the provided math model.

2.  **Simulate Game Play:**
    *   Implement the mechanism to generate random outcomes ("spins") based on the reel strip configurations.
    *   Evaluate the grid outcome to identify winning combinations and calculate payouts according to the payline and paytable rules.
    *   Ensure each round has a consistent bet cost of 10 units.

3.  **Run a Statistical Simulation:**
    *   Build a simulation runner capable of executing **10,000,000 (10M) rounds** efficiently.
    *   Track and aggregate the results to calculate and output:
        *   The actual **Return to Player (RTP %)** achieved during the simulation.
        *   The **Standard Deviation** of the game payouts.

4.  **Produce a Simulation Report:**
    *   The application should output or generate a summary report containing your statistical findings (RTP, Standard Deviation, total rounds, total wagers, total returns) and execution time.

---

## 3. Design & Implementation Guidelines

We want to see how you think and how you design software. To ensure a fair evaluation, we have deliberately left the technical implementation details open-ended. 

> [!IMPORTANT]
> **We are evaluating *how* you build the solution, not just whether it produces the correct numbers.**
> *   Do not hardcode parameters that should be configurable.
> *   Think about thread-safety, performance, and memory usage when scaling up to millions of spins.
> *   Ensure your code is clean, self-documenting, and decoupled.

### Nice to Have
*   **Mathematical Validation:** Your simulated RTP should closely align with the theoretical expectation of the math model.
*   **Unit Testing:** Focus tests on critical parts of the code, such as the evaluation math and symbol positioning logic.
*   **Clean Git History:** Please deliver your project via a public git repository (e.g., GitHub, GitLab) with meaningful commit messages showing your incremental progress.
*   **Clear Documentation:** Include a `README.md` explaining how to compile, run, and test your application, along with your final simulation results.

---

## 4. Going Above and Beyond (Optional Initiatives)

If you want to showcase advanced engineering skills, consider implementing one or more of the following:

*   **Concurrency & Parallelism:** Leverage Java's concurrent utilities to parallelize the 10M-round simulation and measure the performance gains.
*   **Confidence Intervals:** Calculate and report the confidence interval of the simulated RTP to statistically prove the reliability of your run.
*   **Extensibility Showcase:** Design the game evaluation engine so that a developer could easily swap in a new matrix size, different payline shapes, or completely different symbol payouts without rewriting the core simulation loop.
