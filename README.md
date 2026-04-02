# Connect

Connect is a web application designed to help Sacramento State students build better class schedules by combining course information with professor feedback. The app uses professor rating data and AI-generated summaries to give students a faster, clearer way to evaluate instructors while planning their semester.

## Overview

Choosing classes can be stressful and time-consuming, especially when students need to compare multiple professors across different sections. Connect helps simplify that process by gathering professor-related information and turning long-form review content into concise summaries that are easier to understand.

Our goal is to make schedule planning more informed, more efficient, and more student-friendly.

## Features

- Search for courses and professors
- Import professor-related data for specific departments
- Summarize professor descriptions and review-based information using AI
- Help students compare professors while building a schedule
- Backend API support for course and professor data processing

## Tech Stack

### Backend
- Java
- Spring Boot
- Maven
- MySQL

### Frontend
- Add your frontend framework here if applicable

### Tools / Services
- OpenAI API
- GitHub

## How It Works

1. The application imports course or professor-related data for a department.
2. That data is processed and stored in the database.
3. Professor descriptions or related text are passed into an AI summarization workflow.
4. Students can use the results to make better scheduling decisions.

## Getting Started

### Prerequisites

Make sure you have these installed:

- Java 17+ or your required project version
- Maven
- MySQL
- Git

## Environment Variables

This project uses environment variables for sensitive configuration.

Set the following variables locally on your machine:

```bash
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
OPENAI_API_KEY=your_openai_api_key
