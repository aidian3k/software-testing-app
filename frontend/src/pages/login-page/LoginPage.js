import React, { useState } from "react";
import { TextField, Grid, Typography, Button } from "@mui/material";
import {useNavigate} from "react-router-dom";

const LoginPage = () => {
    const [loginData, setLoginData] = useState({
        email: "",
        password: "",
    });
    const navigate = useNavigate();

    const handleChange = (event) => {
        setLoginData({
            ...loginData,
            [event.target.name]: event.target.value,
        });
    };

    const handleLogin = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/user", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const users = await response.json();

            const matchingUser = users.find(
                (user) =>
                    user.email === loginData.email && user.password === loginData.password
            );

            if (matchingUser) {
                console.log("Zalogowano pomyślnie:", matchingUser);
                localStorage.setItem("userId", matchingUser.id);
                navigate('Posts')
            } else {
                console.log("Błędne dane logowania");
            }
        } catch (error) {
            console.error("Błąd podczas logowania:", error.message);
        }
    };

    return (
        <Grid container xs={12} justifyContent="center" pl={5} pr={5} mt={3}>
            <Grid item container xs={7} spacing={2}>
                <Grid item xs={12}>
                    <Typography>Zaloguj się</Typography>
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        type="text"
                        label="Email"
                        fullWidth
                        name="email"
                        value={loginData.email}
                        onChange={handleChange}
                    />
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        type="password"
                        label="Hasło"
                        fullWidth
                        name="password"
                        value={loginData.password}
                        onChange={handleChange}
                    />
                </Grid>
                <Grid item xs={12}>
                    <Button variant="contained" onClick={handleLogin}>
                        Zaloguj się
                    </Button>
                </Grid>
            </Grid>
        </Grid>
    );
};

export default LoginPage;
