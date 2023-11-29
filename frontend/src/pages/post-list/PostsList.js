import React, { useState } from "react";
import { AppBar, Tab, Tabs, Paper } from "@mui/material";
import Posts from "./Posts"; // Importujemy wcześniej utworzony komponent

const sampleUserData = [
    { id: 1, content: "Twój post #1" },
    { id: 2, content: "Twój post #2" },
];

const allPostsData = [
    { id: 3, content: "Wszystki post #1" },
    { id: 4, content: "Wszystki post #2" },
    // ... inne posty
];

const PostsList = () => {
    const [value, setValue] = useState(0);

    const handleChange = (event, newValue) => {
        setValue(newValue);
    };

    return (
        <Paper>
            <AppBar position="static">
                <Tabs value={value} onChange={handleChange}>
                    <Tab label="Twoje Posty" />
                    <Tab label="Wszystkie Posty" />
                </Tabs>
            </AppBar>
            <TabPanel value={value} index={0}>
                <Posts posts={sampleUserData} />
            </TabPanel>
            <TabPanel value={value} index={1}>
                <Posts posts={allPostsData} />
            </TabPanel>
        </Paper>
    );
};

const TabPanel = (props) => {
    const { children, value, index, ...other } = props;

    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`tabpanel-${index}`}
            {...other}
        >
            {value === index && <div>{children}</div>}
        </div>
    );
};

export default PostsList;
