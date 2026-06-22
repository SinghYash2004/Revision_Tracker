# DSA Revision Tracker - Enhanced Version

A modern, aesthetic, and fully authenticated personal interview preparation system with spaced repetition tracking.

## 🎨 What's New

### UI/UX Improvements
- **Modern Design**: Beautiful gradient color scheme (blue/purple)
- **Professional Login Screen**: Secure authentication interface
- **Enhanced Sidebar**: User profile display with logout option
- **Responsive Layout**: Works seamlessly on desktop, tablet, and mobile
- **Smooth Animations**: Hover effects, transitions, and interactive elements
- **Better Typography**: Improved readability and visual hierarchy

### User Authentication
- **User Accounts**: Create an account with email and password
- **Secure Login**: SHA-256 password hashing with salt
- **Session Persistence**: Your progress is saved even after closing the browser
- **Quick Re-login**: Automatic session restoration on page reload
- **Logout**: Clear session with one click

### Data Management
- **Personal Dashboard**: View your readiness score, streaks, and progress
- **Topic Management**: Add topics with confidence levels and revision tracking
- **Problem Solving**: Track problems by platform, difficulty, and tags
- **Revision System**: Spaced repetition with hidden notes until recall
- **Analytics**: View your progress with charts and heatmaps
- **Export**: Download your data as JSON or CSV files

## 🚀 Getting Started

### Running the Application

```bash
# Build the project
mvn clean package

# Run the application
java -jar target/dsa-revision-tracker-1.0.0.jar
```

The application will be available at `http://localhost:8080`

### First Time User

1. Go to `http://localhost:8080`
2. Click **"Sign up here"** to create a new account
3. Enter your name, email, and password (min 4 characters)
4. Click **"Sign Up"**
5. Your account is created! You'll be automatically logged in

### Returning User

1. Go to `http://localhost:8080`
2. Enter your email and password
3. Click **"Login"**
4. Your previous progress will be automatically loaded

### Creating a Topic

1. Navigate to the **Topics** section
2. Fill in the form:
   - **Topic name**: e.g., "Binary Search"
   - **Category**: e.g., "Arrays", "Trees", "Searching"
   - **Date Learned**: When you learned this topic
   - **Confidence Level**: 1-5 (5 = most confident)
   - **Notes**: Your study notes
3. Click **"Add Topic"**
4. The system automatically generates a revision schedule

### Adding a Problem

1. Go to **Problems** section
2. Fill in the details:
   - **Platform**: LeetCode, HackerRank, CodeSignal, etc.
   - **Problem Number**: e.g., "1"
   - **Problem Name**: e.g., "Two Sum"
   - **Difficulty**: Easy, Medium, or Hard
   - **Tags**: Related topics (separated by ;)
   - **Solved Date**: When you solved it
   - **Time Taken**: How long it took
   - **Used Hint**: Whether you used hints
   - **Solved Independently**: If you solved it without help
   - **Personal Notes**: Your approach and learnings
3. Click **"Add Problem"**

### Completing Revisions

1. Go to **Today's Revisions** section
2. You'll see topics/problems due for revision
3. Try to recall without looking at notes
4. Select your recall level:
   - ✅ **I solved it** - Easy recall
   - ⚡ **Partial recall** - Some confusion
   - ❌ **Forgot** - Completely forgotten
5. After submitting, the notes are revealed
6. The next revision date is automatically scheduled

### Tracking Progress

- **Dashboard**: Overall readiness score and current streak
- **Analytics**: Problems and topics per month, tag distribution
- **Progress**: Activity heatmap and forgotten topics detector
- **Weak Areas**: Topics that need more practice

## 📊 Features

- **Spaced Repetition**: Intelligently scheduled revisions based on your recall
- **Streak Tracking**: Maintain your learning streak
- **Tag-Based Organization**: Organize problems by topics and tags
- **Analytics Dashboard**: Visualize your learning progress
- **Heatmap**: See your activity over the last 6 months
- **Recommendations**: Get smart suggestions on what to revise
- **Data Export**: Export all your data as JSON or CSV
- **Multi-User Support**: Each user has their own isolated data

## 💾 Data Storage

All data is stored locally in CSV files:
- `users.csv` - User accounts and authentication
- `topics.csv` - Learned topics
- `problems.csv` - Practice problems
- `revisions.csv` - Active revision schedules
- `revision_history.csv` - Completed revisions
- `streaks.csv` - Streak information

## 🔐 Security

- **Password Hashing**: SHA-256 with salt
- **Session Management**: Secure localStorage-based sessions
- **User Isolation**: Each user only sees their own data
- **Input Validation**: All inputs are validated and sanitized

## 📱 Responsive Design

The application works on all devices:
- **Desktop**: Full feature-rich experience
- **Tablet**: Optimized layout with touch support
- **Mobile**: Compact sidebar, optimized forms

## 🎯 Best Practices

1. **Consistent Tagging**: Use consistent tags for better organization
2. **Regular Revisions**: Complete your daily revisions
3. **Detailed Notes**: Write comprehensive notes for future reference
4. **Honest Assessment**: Rate your recall honestly for better scheduling
5. **Export Regularly**: Backup your data periodically

## 🐛 Troubleshooting

**Q: I forgot my password**
A: Currently, passwords cannot be reset. Create a new account with a different email.

**Q: My data disappeared after logout**
A: Make sure to use the same email and password when logging back in.

**Q: The app won't load**
A: Clear your browser cache or try incognito mode.

**Q: Why is my revision date so far away?**
A: The app uses spaced repetition. Dates increase based on your recall performance.

## 🚀 Future Enhancements

- Password reset functionality
- Email notifications for revisions due
- Mobile app
- Team/study group features
- Integration with LeetCode API
- Dark mode
- Achievements and badges

## 📝 License

This project is open source and available for personal use.

## 🤝 Contributing

Contributions are welcome! Feel free to submit issues and enhancement requests.

---

**Happy learning! Keep your streak alive! 🔥**
