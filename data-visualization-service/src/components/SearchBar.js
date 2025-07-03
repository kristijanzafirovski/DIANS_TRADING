import React, { useState } from 'react';
import './SearchBar.css';

function SearchBar({ onSearch }) {
  const [input, setInput] = useState('');
  const submit = (e) => {
    e.preventDefault();
    if (input) {
      onSearch(input);
      setInput('');
    }
  };
  return (
    <form className="search-bar" onSubmit={submit}>
      <input
        type="text"
        placeholder="Search ticker..."
        value={input}
        onChange={e => setInput(e.target.value)}
      />
      <button type="submit">Add</button>
    </form>
  );
}

export default SearchBar;
