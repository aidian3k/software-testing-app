import React from 'react';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Button from '@mui/material/Button';
import AppRoutes from './routes/routes';
import { AppBar, Toolbar } from '@mui/material';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976D2', // Główny kolor dla przycisków i innych elementów zaznaczalnych
    },
    secondary: {
      main: '#FF4081', // Kolor pomocniczy
    },
  },
  typography: {
    fontFamily: 'Roboto, sans-serif', // Domyślna czcionka dla tekstu
    h1: {
      fontSize: '2rem',
      fontWeight: 600,
    },
    h2: {
      fontSize: '1.5rem',
      fontWeight: 500,
    },
    h3: {
      fontSize: '1.17rem',
      fontWeight: 400,
    },
  },
});

const Home = () => (
  <div>
    <h1>Strona Główna</h1>
    <Button variant="contained" color="primary">
      Primary Button
    </Button>
  </div>
);

const About = () => (
  <div>
    <h1>O Nas</h1>
    <p>Tutaj umieść treść strony "O Nas".</p>
  </div>
);

function App() {
  return (
    <ThemeProvider theme={theme}>
      <Router>
        <CssBaseline />
        <div>
          <AppBar position="static">
            <Toolbar>
              <Button component={Link} to="/" color="inherit">
                Strona Główna
              </Button>
              <Button component={Link} to="/about" color="inherit">
                O Nas
              </Button>
              <Button component={Link} to="/login" color="inherit">
                Zaloguj się
              </Button>
              <Button component={Link} to="/register" color="inherit">
                Zarejestruj się
              </Button>
              <Button component={Link} to="/posts" color="inherit">
                Posty
              </Button>
            </Toolbar>
          </AppBar>
        </div>
        <AppRoutes />
      </Router>
    </ThemeProvider>
  );
}

export default App;
