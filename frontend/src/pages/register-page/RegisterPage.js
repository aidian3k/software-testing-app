import React, { useState } from "react";
import { TextField, Grid, Typography, Button } from "@mui/material";

const RegisterPage = () => {
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        surname: "",
        password: "",
    });

    const handleChange = (event) => {
        setFormData({
            ...formData,
            [event.target.name]: event.target.value,
        });
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        // Tutaj możesz dodać kod obsługujący wysyłanie danych do backendu (np. używając fetch lub axios)
        console.log("Dane do wysłania:", formData);
    };

    return (
        <Grid container xs={12} justifyContent="center" pl={5} pr={5} mt={3}>
            <Grid item container xs={7} spacing={2}>
                <Grid item xs={12}>
                    <Typography>Zarejestruj się</Typography>
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        type="text"
                        label="Imię"
                        fullWidth
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                    />
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        type="text"
                        label="Nazwisko"
                        fullWidth
                        name="surname"
                        value={formData.surname}
                        onChange={handleChange}
                    />
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        type="email"
                        label="Email"
                        fullWidth
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                    />
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        type="password"
                        label="Hasło"
                        fullWidth
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                    />
                </Grid>
                <Grid item xs={12}>
                    <Button variant="contained" onClick={handleSubmit}>
                        Zarejestruj się
                    </Button>
                </Grid>
            </Grid>
        </Grid>
    );
};

export default RegisterPage;
