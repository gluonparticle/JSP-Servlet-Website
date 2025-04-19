#!/usr/bin/env python3
import os
import shutil
import subprocess
from datetime import datetime, timedelta, timezone # Added timezone
import random 

# --- Configuration ---
PROJECT_DIR = "/home/gluonparticle/JSP-Servlet-Website"
GIT_AUTHOR_NAME = "Sargun Singh"
GIT_AUTHOR_EMAIL = "gluonparticle@gmail.com"

START_DATETIME_STR = "2025-04-19 07:43:09" # Assumed to be UTC

PHASE_STRUCTURE = [
    (6, 2), (4, 1), (5, 0)
]
COMMITS_PER_DAY_PHASE1 = [3, 3, 3, 4, 3, 3]
COMMITS_PER_DAY_PHASE2 = [3, 3, 4, 3]
COMMITS_PER_DAY_PHASE3 = [3, 3, 3, 3, 3]
ALL_COMMITS_PER_DAY_SCHEDULE = COMMITS_PER_DAY_PHASE1 + COMMITS_PER_DAY_PHASE2 + COMMITS_PER_DAY_PHASE3

COMMIT_MESSAGES = [
    "Initial project setup and add all existing project files", "Add .gitignore and README.md",
    "Create base JSP files: index.jsp, header.jsp, footer.jsp",
    "Implement Database Connection utility class (Database.java)", "Create Account entity and AccountDao for user management",
    "Develop basic User Registration servlet (RegisterControl) and register.jsp",
    "Develop User Login servlet (LoginControl) and login.jsp", "Implement Logout functionality (LogoutControl)",
    "Add session management for logged-in users",
    "Create Product entity and ProductDao for product data", "Develop servlet to display all products (HomeControl/ShopControl)",
    "Design product_list.jsp to display products", "Add basic CSS styling for homepage and product list",
    "Create Category entity and CategoryDao", "Implement product filtering by category",
    "Style category navigation and product cards",
    "Implement product detail page (detail.jsp) and DetailControl servlet", "Add 'Add to Cart' functionality (CartControl, cart.jsp)",
    "Refactor common JSP parts into includes (e.g., navigation)",
    "Implement Cart viewing and item quantity update in cart.jsp", "Implement 'Remove from Cart' functionality",
    "Add basic input validation for registration and login forms",
    "Create Order entity and OrderDao", "Develop Checkout process: checkout.jsp and CheckoutControl servlet",
    "Store order details in the database upon successful checkout",
    "Implement Order History page for users (order_history.jsp, OrderHistoryControl)", "Display order details on order history page",
    "Improve error handling in DAOs and Servlets", "Refactor Dao classes to use try-with-resources for connection management",
    "Implement basic search functionality (SearchControl, search_results.jsp)", "Add pagination to product listing page",
    "Style search results and pagination controls",
    "Create Admin section: basic admin dashboard (admin_dashboard.jsp)",
    "Implement Product Management for Admin: Add Product (AdminProductAddControl)",
    "Implement Product Management for Admin: View/List Products",
    "Implement Product Management for Admin: Edit Product (AdminProductEditControl)",
    "Implement Product Management for Admin: Delete Product", "Add user role management (e.g., 'admin', 'customer')",
    "Secure Admin routes, accessible only by admin users", "Refine UI/UX across the site: consistent styling, better navigation",
    "Add Javadoc comments to critical classes and methods",
    "Implement basic unit tests for Dao classes (e.g., AccountDaoTest)", "Optimize database queries for performance",
    "Update all dependencies in pom.xml to stable versions",
    "Final code cleanup and refactoring", "Perform thorough testing of all features",
    "Prepare deployment scripts/notes and final README update"
]
# --- End Configuration ---

def run_command(command, cwd, env=None): # Added env parameter
    """Executes a shell command."""
    print(f"Executing: {' '.join(command)} in {cwd}")
    # Use provided env or default to current os.environ if None
    effective_env = env if env is not None else os.environ.copy()
    result = subprocess.run(command, cwd=cwd, capture_output=True, text=True, env=effective_env)
    if result.returncode != 0:
        print(f"Error output: {result.stderr}")
        if result.stdout:
            print(f"Standard output: {result.stdout}")
        raise Exception(f"Command failed: {' '.join(command)}")
    return result

def main():
    if not os.path.isdir(PROJECT_DIR):
        print(f"Error: Project directory {PROJECT_DIR} does not exist.")
        return

    if len(COMMIT_MESSAGES) != sum(ALL_COMMITS_PER_DAY_SCHEDULE):
        print(f"Error: Mismatch in commit message count ({len(COMMIT_MESSAGES)}) and scheduled commits ({sum(ALL_COMMITS_PER_DAY_SCHEDULE)}).")
        return
    
    print(f"Total commits to be made: {len(COMMIT_MESSAGES)}")

    original_cwd = os.getcwd()
    os.chdir(PROJECT_DIR)
    print(f"Changed directory to {PROJECT_DIR}")

    if os.path.exists(".git"):
        print("Removing existing .git directory...")
        try:
            for root, dirs, files in os.walk(".git"):
                for d in dirs: os.chmod(os.path.join(root, d), 0o777)
                for f in files: os.chmod(os.path.join(root, f), 0o777)
            shutil.rmtree(".git")
        except Exception as e:
            print(f"Could not remove .git directory: {e}. Attempting 'git init' anyway.")

    run_command(["git", "init"], cwd=PROJECT_DIR)
    run_command(["git", "config", "user.name", GIT_AUTHOR_NAME], cwd=PROJECT_DIR)
    run_command(["git", "config", "user.email", GIT_AUTHOR_EMAIL], cwd=PROJECT_DIR)
    # Set default branch to main if git version might default to master
    run_command(["git", "branch", "-M", "main"], cwd=PROJECT_DIR)


    print("Adding all existing project files to the staging area for the first commit...")
    run_command(["git", "add", "."], cwd=PROJECT_DIR)

    # Initialize current_datetime as UTC-aware
    current_datetime_naive = datetime.strptime(START_DATETIME_STR, "%Y-%m-%d %H:%M:%S")
    current_datetime = current_datetime_naive.replace(tzinfo=timezone.utc) # Make it UTC aware

    commit_message_index = 0
    working_day_counter = 0
    history_log_file = "project_evolution_log.txt"

    with open(history_log_file, "w") as f:
        f.write(f"Git history generation started at {datetime.now(timezone.utc)}\n\n")
    run_command(["git", "add", history_log_file], cwd=PROJECT_DIR)

    for phase_idx, (work_days, break_days) in enumerate(PHASE_STRUCTURE):
        print(f"\n--- Phase {phase_idx + 1}: {work_days} working day(s), {break_days} break day(s) ---")
        for day_in_phase in range(work_days):
            if working_day_counter >= len(ALL_COMMITS_PER_DAY_SCHEDULE):
                break
            
            num_commits_today = ALL_COMMITS_PER_DAY_SCHEDULE[working_day_counter]
            print(f"Working Day (Overall {working_day_counter + 1}): {current_datetime.strftime('%Y-%m-%d %Z')}, Commits: {num_commits_today}")

            if working_day_counter == 0 and day_in_phase == 0 :
                day_commit_start_time = current_datetime # Already UTC aware
            else:
                day_commit_start_time_naive = current_datetime.replace(
                    hour=random.randint(7, 9), 
                    minute=random.randint(0, 59), 
                    second=random.randint(0, 59),
                    microsecond=0
                )
                day_commit_start_time = day_commit_start_time_naive # Already UTC aware from current_datetime

            last_commit_time_on_day = day_commit_start_time
            
            for i in range(num_commits_today):
                if commit_message_index >= len(COMMIT_MESSAGES):
                    break

                commit_message = COMMIT_MESSAGES[commit_message_index]
                
                if i == 0 and not (working_day_counter == 0 and day_in_phase == 0):
                    commit_dt = last_commit_time_on_day
                elif i == 0 and working_day_counter == 0 and day_in_phase == 0:
                    commit_dt = last_commit_time_on_day
                else:
                    time_increment_seconds = random.randint(30 * 60, 3 * 60 * 60)
                    commit_dt = last_commit_time_on_day + timedelta(seconds=time_increment_seconds)

                if commit_dt.hour >= 20: # If time goes past 8 PM UTC
                    # Bring it back to a reasonable time on the same day, still UTC
                    commit_dt = commit_dt.replace(hour=random.randint(18,19), minute=random.randint(0,59))
                
                if commit_dt.date() != current_datetime.date(): # Ensure it's still on the same UTC day
                    commit_dt = current_datetime.replace(
                        hour=last_commit_time_on_day.hour + 1 if last_commit_time_on_day.hour < 19 else 19, 
                        minute=random.randint(0,59),
                        second=random.randint(0,59)
                    )
                
                # Prepare date string for environment variables (Git internal format: <unix_timestamp> <offset>)
                # commit_dt is already UTC aware
                git_env_date_str = f"{int(commit_dt.timestamp())} {commit_dt.strftime('%z')}"

                commit_env = os.environ.copy()
                commit_env["GIT_AUTHOR_DATE"] = git_env_date_str
                commit_env["GIT_COMMITTER_DATE"] = git_env_date_str

                if not (working_day_counter == 0 and day_in_phase == 0 and i == 0):
                    with open(history_log_file, "a") as f:
                        f.write(f"Change for commit {commit_message_index + 1} on {commit_dt.isoformat()}: {commit_message}\n")
                    run_command(["git", "add", history_log_file], cwd=PROJECT_DIR, env=commit_env) # Pass env here too for add if needed, though not strictly
                else:
                    with open(history_log_file, "a") as f:
                        f.write(f"Initial state for commit {commit_message_index + 1} on {commit_dt.isoformat()}: {commit_message}\n")
                    # For the first commit, files are already staged by 'git add .'
                    # The history_log_file was also added. No need to 'git add' again here.

                print(f"  Committing {commit_message_index + 1}/{len(COMMIT_MESSAGES)}: '{commit_message}' with date {commit_dt.isoformat()} (Env: {git_env_date_str})")
                # Commit using the environment variables for dates
                run_command(["git", "commit", "-m", commit_message], cwd=PROJECT_DIR, env=commit_env)
                
                last_commit_time_on_day = commit_dt
                commit_message_index += 1
            
            current_datetime += timedelta(days=1) # current_datetime remains UTC aware
            working_day_counter += 1

        if break_days > 0:
            print(f"Break: {break_days} day(s) from {current_datetime.strftime('%Y-%m-%d %Z')}")
            current_datetime += timedelta(days=break_days)
        
        if commit_message_index >= len(COMMIT_MESSAGES):
            break

    print("\n--- Git history generation complete! ---")
    print(f"Total commits made: {commit_message_index}")
    print(f"You can now go to {PROJECT_DIR} and run 'git log --pretty=fuller'")
    
    os.chdir(original_cwd)

if __name__ == "__main__":
    main()