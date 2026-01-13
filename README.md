# Insurance Company

A Java-based educational domain model of an insurance company that focuses on contracts, premium calculation, payment processing, and claim handling. The model covers vehicle insurance (single-vehicle contracts and master contracts for legal entities with child contracts) and travel insurance for groups of natural persons.

## Project Goal

This project demonstrates how core insurance workflows can be modeled with clean, domain-driven classes. It is intended for study, assignments, and unit testing scenarios where you need to simulate insurance operations such as:

- **Creating contracts** for vehicles and travel coverage
- **Charging premiums** based on payment frequency and current time
- **Processing payments** and tracking payment history
- **Handling claims** with rules that deactivate contracts when needed

## Domain Model Overview

The system is centered around `InsuranceCompany`, which owns contracts and orchestrates premium charging, payments, and claims. Contracts are represented by a hierarchy that captures different insurance products:

- **Vehicle contracts** for single vehicles, with optional beneficiaries
- **Master contracts** for legal entities that aggregate multiple vehicle contracts
- **Travel contracts** that insure a group of natural persons

The model also includes shared entities for people, vehicles, and payment data that support validation and business rules.

## Key Features

- **Contract creation with validation**: Enforces unique contract numbers, legal-form rules, and minimum premium thresholds.
- **Premium scheduling**: Tracks next payment dates and accumulates outstanding balances as time advances.
- **Payment processing**: Records payment instances and distributes master-contract payments across child contracts.
- **Claim handling rules**: Pays coverage to beneficiaries or policy holders and deactivates contracts based on damage thresholds.

## Project Structure

```
insurance_company/
└── src/
    ├── company/                # Core insurer behavior
    ├── contracts/              # Contract types and exceptions
    ├── objects/                # Person, vehicle, legal form
    └── payment/                # Payment data, handler, history
```

## Package Breakdown

### `company`
- `InsuranceCompany` — the main service that stores active contracts, holds the current time, creates contracts, charges premiums, and processes claims.

### `contracts`
- `AbstractContract` — base contract with number, insurer, policy holder, payment data, coverage amount, and active status.
- `AbstractVehicleContract` — vehicle contract base with a beneficiary.
- `SingleVehicleContract` — contract for a single insured vehicle.
- `MasterVehicleContract` — contract for a legal entity that aggregates multiple vehicle contracts.
- `TravelContract` — travel insurance contract for a set of natural persons.
- `InvalidContractException` — domain exception for invalid contract operations.

### `objects`
- `Person` — contract participant (policy holder, beneficiary, insured person). Determines legal form from ID, stores paid-out amounts and owned contracts.
- `Vehicle` — vehicle model with license plate and original value.
- `LegalForm` — enum for natural vs. legal persons.

### `payment`
- `ContractPaymentData` — premium data: amount, frequency, next payment time, and outstanding balance.
- `PaymentHandler` — payment processing and payment history for contracts; supports master contract payments.
- `PaymentInstance` — single payment record with time and amount.
- `PremiumPaymentFrequency` — payment frequency (annual, semi-annual, quarterly, monthly).

## Core Workflows

### Vehicle Insurance (Single Contract)
1. Validate input data, payment frequency, and unique contract number.
2. Calculate annual premium based on payment frequency.
3. Ensure the annual premium is at least 2% of the vehicle’s original value.
4. Create a `SingleVehicleContract` with coverage set to half the vehicle’s value.
5. Charge premium based on current time and attach the contract to the policy holder.

### Travel Insurance
1. Validate input data and ensure insured persons are all natural persons.
2. Calculate the annual premium and ensure it is at least 5 per insured person.
3. Create a `TravelContract` with coverage set to 10 per insured person.
4. Charge premium and attach the contract to the policy holder.
5. When a claim is processed, the contract is closed after payout.

### Master Vehicle Contract
1. Create a `MasterVehicleContract` for a legal entity policy holder.
2. Move existing `SingleVehicleContract` instances under the master contract.
3. Process payments that are distributed across active child contracts.

### Claim Processing
- **Vehicle claims**: Pays the full coverage amount to the beneficiary (if present) or the policy holder. If damages are at least 70% of vehicle value, the contract becomes inactive.
- **Travel claims**: Splits the coverage amount across affected insured persons and deactivates the contract.

## Business Rules Summary

- Contract numbers must be unique per insurer.
- Premiums must be positive and meet minimum annual thresholds.
- Legal entities can hold master vehicle contracts; natural persons cannot.
- Travel contracts can only insure natural persons.
- Payments and claims are allowed only for active contracts owned by the insurer.

## Usage

This project has no executable entry point (no `main`) and is intended as a library for study, tests, or integration into a larger application. Create instances of `InsuranceCompany`, `Person`, and `Vehicle`, then build contracts and invoke premium charging, payment handling, and claim processing methods as needed.

## Notes

This repository is intentionally minimal and focused on the domain model. It does not include persistence, a user interface, or external integrations.
