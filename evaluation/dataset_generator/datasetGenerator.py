import csv
import random
from datetime import datetime, timedelta

num_jobs = 100
filename = "JobSampleData" + str(num_jobs) + ".csv"

base_date = datetime(2026, 1, 5, 7, 0)

work_start_hour = 7
work_end_hour = 22
max_days_ahead = 7
probability = 0.7


def generate_deadline():
    day_offset = random.randint(0, max_days_ahead)
    base_day = base_date + timedelta(days=day_offset)
    minutes_array = [0, 15, 30, 45]

    if random.random() < probability:
        hour = random.randint(work_start_hour, work_end_hour - 1)
        minute = random.choice(minutes_array)
    else:
        if random.choice([True, False]):
            hour = random.randint(0, work_start_hour - 1)
        else:
            hour = random.randint(work_end_hour, 23)
        minute = random.choice(minutes_array)
    return base_day.replace(hour=hour, minute=minute)


def generate_duration():
    return random.choice([30, 45, 60, 90, 120, 180, 240, 300])

def generate_profit():
    return random.randint(50, 900)


with open(filename, mode="w", newline="", encoding="utf-8") as file:
    writer = csv.writer(file, delimiter=";")
    writer.writerow(["ID", "Trvanie", "Deadline", "Profit"])
    for i in range(1, num_jobs + 1):
        duration = generate_duration()
        deadline = generate_deadline()
        profit = generate_profit()
        writer.writerow([
            f"Task{i}",
            duration,
            deadline.strftime("%d/%m/%Y %H:%M"),
            profit
        ])
