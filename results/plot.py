import matplotlib.pyplot as plt
import numpy as np

algo1_data = [
    (20, 7.8348429, 529),
    (30, 3.924273, 605),
    (40, 3.7151076, 738),
    (50, 3.8062275, 862),
    (60, 4.1706749, 929),
    (70, 4.8940296, 1047),
    (80, 4.8414271, 987)
]

algo2_data = [
    (20, 2.2644546, 588),
    (30, 2.4391089, 769),
    (40, 2.3506045, 933),
    (50, 2.1609875, 1080),
    (60, 2.2357748, 955),
    (70, 2.5071211, 1271),
    (80, 1.9188149, 1357)
]

# Extraction des données pour l'axe x (nombre d'objets)
x = [d[0] for d in algo1_data]

# Extraction des données pour les temps d'exécution et les évaluations
algo1_times = [d[1] for d in algo1_data]
algo2_times = [d[1] for d in algo2_data]
algo1_evals = [d[2] for d in algo1_data]
algo2_evals = [d[2] for d in algo2_data]

# Création du graphique
fig, ax1 = plt.subplots(figsize=(12, 6))

# Barres pour les temps d'exécution
ax1.bar(np.array(x) - 1, algo1_times, width=1, align='center', label='GA (Temps d\'exécution)', alpha=0.7)
ax1.bar(np.array(x) + 1, algo2_times, width=1, align='center', label='BSO (Temps d\'exécution)', alpha=0.7)
ax1.set_xlabel('Nombre de sacs')
ax1.set_ylabel('Temps d\'exécution (s)', color='tab:blue')
ax1.tick_params('y', colors='tab:blue')
ax1.legend(loc='upper left')

# Création de la deuxième échelle y pour les évaluations
ax2 = ax1.twinx()
ax2.plot(x, algo1_evals, color='red', linestyle='-', marker='o', label='Évaluation - GA')
ax2.plot(x, algo2_evals, color='green', linestyle='-', marker='o', label='Évaluation - BSO')
ax2.set_ylabel('Évaluation', color='black')
ax2.tick_params('y', colors='black')
ax2.legend(loc='upper right')

plt.text(32, 1200, 'Nombre d\'objets = 100', color='black', fontsize=10, ha='right')

# Titre et grille
plt.title('Comparaison des temps d\'exécution et des évaluations (GA vs BSO)')
plt.grid(True)

# Affichage du graphique
plt.show()
