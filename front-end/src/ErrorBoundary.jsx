import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    this.logErrorToLocalStorage(error, errorInfo);
    this.sendLogToServer();
  }

  logErrorToLocalStorage(error, errorInfo) {
    const logs = JSON.parse(localStorage.getItem('errorLogs')) || [];
    const date = new Date();
    const dateString = date.toISOString().split('T')[0];
    const timeString = date.toTimeString().split(' ')[0];

    logs.push({
      error: error.toString(),
      errorInfo: errorInfo.componentStack,
      date: dateString,
      time: timeString
    });
    localStorage.setItem('errorLogs', JSON.stringify(logs));
  }

  async sendLogToServer() {
    const logs = JSON.parse(localStorage.getItem('errorLogs')) || [];
    if (logs.length > 0) {
      try {
        const response = await fetch('http://localhost:8091/sample/log-error', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(logs),
        });
        if (response.ok) {
          localStorage.removeItem('errorLogs');
        }
      } catch (err) {
        console.error('Error sending log to server:', err);
      }
    }
  }

  render() {
    if (this.state.hasError) {
      return <h1>Something went wrong.</h1>;
    }
    return this.props.children;
  }
}

export default ErrorBoundary;
